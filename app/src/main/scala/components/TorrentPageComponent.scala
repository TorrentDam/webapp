package components

import org.scalajs.dom
import com.github.lavrov.bittorrent.app.protocol.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Message.{TorrentError, TorrentMetadataReceived, TorrentStats, RequestTorrent}
import com.github.lavrov.bittorrent.app.protocol.{Message, Event}
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.{ReactiveHtmlElement, ReactiveSvgElement}
import routing.Page
import squants.information.Bytes
import util.{Config, InformationFormatter, MagnetLink}


def TorrentPageComponent(
  magnetLink: MagnetLink,
  send: Observer[RequestTorrent],
  events: EventStream[TorrentMetadataReceived | TorrentStats | TorrentError],
  fileIndex: Option[Int],
  metadataCache: Var[Map[InfoHash, TorrentMetadataReceived]]
) =
  val infoHash = magnetLink.infoHash

  val currentTab = Switch["files" | "magnet"]("files")

  val showModalVar = Var(Option.empty[ActiveFile])
  val loadingVar = Var(true)
  val torrentMetadataVar = Var(metadataCache.now().get(infoHash))
  val torrentStatsVar = Var(Option.empty[TorrentStats])
  val torrentErrorVar = Var(Option.empty[String])
  val fileFilterVar = Var("")
  val copiedVar = Var(Option.empty[String])

  val navigateObserver = Observer[Page](page => routing.router.pushState(page))

  val connectedPeerCount = torrentStatsVar.signal.map(_.map(_.connected).getOrElse(0))

  def availabilityPercent(index: Int): Signal[Int] =
    torrentStatsVar.signal
      .map {
        case Some(stats) => stats.availability.lift(index).getOrElse(0.0)
        case None => 0.0
      }
      .map(_ * 100)
      .map(_.toInt)

  def videoUrl(index: Int) =
    Config.httpUrl(s"torrent/$infoHash/data/$index")

  val fileClickObserver = Observer[(Int, String)] { case (index, name) =>
    if isVideoFile(name) then
      routing.router.pushState(Page.Torrent(magnetLink.toMagnetUri, Some(index)))
    else
      showModalVar.set(Some(ActiveFile(name, videoUrl(index))))
  }

  val copyTextObserver = Observer[(String, String)] { case (key, text) =>
    dom.window.navigator.clipboard.writeText(text)
    copiedVar.set(Some(key))
    dom.window.setTimeout(() => copiedVar.set(None), 1500)
  }

  def fileRow(file: Message.File, index: Int) =
    val fileName = file.path.last
    val ext = fileName.split('.').lastOption.filter(_.length <= 5).map(_.toUpperCase).getOrElse("FILE")
    val copyKey = s"file:$index"
    div(cls := "media file-row",
      div(cls := "media-left",
        span(cls := "icon has-text-grey", fileIcon(fileName))
      ),
      div(cls := "media-content",
        p(cls := "subtitle is-6 mb-1",
          a(
            file.path.mkString,
            onClick.mapTo((index, fileName)) --> fileClickObserver,
          )
        ),
        div(cls := "is-flex is-align-items-center is-gap-2",
          progressTag(cls := "progress is-small is-primary file-availability",
            valueAttr <-- availabilityPercent(index).map(_.toString),
            maxAttr := "100"
          ),
          span(cls := "is-size-7 has-text-grey", child <-- availabilityPercent(index).map(p => s"$p%"))
        )
      ),
      div(cls := "media-right",
        div(cls := "file-actions",
          span(cls := "is-size-7", renderBytes(file.size)),
          a(cls := "button is-small is-light",
            href := videoUrl(index),
            target := "_blank",
            download := fileName,
            span(cls := "icon", Icons.download),
            span("Download")
          ),
          button(cls := "button is-small is-light",
            cls.toggle("is-success") <-- copiedVar.signal.map(_.contains(copyKey)),
            onClick.mapTo((copyKey, videoUrl(index))) --> copyTextObserver,
            span(cls := "icon", Icons.clipboard),
            span(child <-- copiedVar.signal.map(c => if c.contains(copyKey) then "Copied!" else "Copy URL"))
          )
        )
      )
    )

  def filesTab(files: List[Message.File]) =
    val filtered = fileFilterVar.signal.map { filter =>
      val f = filter.trim.toLowerCase
      files.zipWithIndex.filter { case (file, _) =>
        f.isEmpty || file.path.mkString("/").toLowerCase.contains(f)
      }
    }
    div(
      div(cls := "field mb-4",
        div(cls := "control has-icons-left file-filter",
          input(
            cls := "input",
            typ := "text",
            placeholder := "Filter files…",
            aria.label := "Filter files",
            value <-- fileFilterVar.signal,
            onInput.mapToValue --> fileFilterVar
          ),
          span(cls := "icon is-left", Icons.search)
        )
      ),
      div(
        children <-- filtered.map {
          case Nil =>
            List(div(cls := "has-text-centered has-text-grey py-6", "No files match your filter"))
          case rows =>
            rows.map { case (file, index) => fileRow(file, index) }
        }
      )
    )

  def magnetTab =
    val magnetUri = magnetLink.toMagnetUri
    div(
      div(cls := "columns",
        div(cls := "column is-one-quarter", span(cls := "has-text-weight-bold", "Info-Hash")),
        div(cls := "column is-family-monospace", magnetLink.infoHash.toString),
      ),
      div(cls := "columns",
        div(cls := "column is-one-quarter", span(cls := "has-text-weight-bold", "Display name")),
        div(cls := "column", magnetLink.displayName.getOrElse("")),
      ),
      div(cls := "columns",
        div(cls := "column is-one-quarter", span(cls := "has-text-weight-bold", "Trackers")),
        div(cls := "column", magnetLink.trackers.map(t => p(cls := "is-family-monospace word-break", t))),
      ),
      div(cls := "columns",
        div(cls := "column is-one-quarter", span(cls := "has-text-weight-bold", "Magnet link")),
        div(cls := "column",
          div(cls := "box is-family-monospace word-break mb-3", magnetUri),
          button(cls := "button is-small is-light",
            cls.toggle("is-success") <-- copiedVar.signal.map(_.contains("magnet")),
            onClick.mapTo(("magnet", magnetUri)) --> copyTextObserver,
            span(cls := "icon", Icons.clipboard),
            span(child <-- copiedVar.signal.map(c => if c.contains("magnet") then "Copied!" else "Copy magnet link"))
          )
        ),
      ),
    )

  def errorNotification(message: String) =
    div(cls := "notification is-danger is-light",
      button(cls := "delete", aria.label := "Dismiss", onClick --> torrentErrorVar.toObserver.contramap(_ => None)),
      s"Torrent error: $message"
    )

  def loadingSkeleton =
    div(
      div(cls := "skeleton-block", styleAttr := "height: 2.5rem; width: 40%; margin-bottom: 1.5rem"),
      div(cls := "skeleton-lines mb-5", div(), div(), div()),
      div(cls := "skeleton-block", styleAttr := "height: 2.25rem; margin-bottom: 1rem"),
      (1 to 5).map(_ => div(cls := "skeleton-block", styleAttr := "height: 3.5rem")).toList
    )

  def mainContent(metadata: TorrentMetadataReceived) =
    div(
      h4(cls := "title is-4",
        onMountCallback(_ => loadingVar.set(false)),
        metadata.name
      ),
      div(cls := "stats-row mb-4",
        div(cls := "stat",
          p(cls := "heading", "Files"),
          p(cls := "subtitle", metadata.files.size)
        ),
        div(cls := "stat",
          p(cls := "heading", "Size"),
          p(cls := "subtitle", renderBytes(metadata.files.map(_.size).sum))
        ),
        div(cls := "stat",
          p(cls := "heading", "Peers"),
          p(cls := "subtitle", child <-- connectedPeerCount.map(_.toString))
        )
      ),
      div(cls := "tabs",
        ul(
          li(
            cls.toggle("is-active") <-- currentTab.port("files"),
            a("Files", onClick.mapTo("files") --> currentTab.activate)
          ),
          li(
            cls.toggle("is-active") <-- currentTab.port("magnet"),
            a("Magnet", onClick.mapTo("magnet") --> currentTab.activate)
          ),
        )
      ),
      div(
        child <-- currentTab.active.map {
          case "files" => filesTab(metadata.files)
          case "magnet" => magnetTab
        }
      )
    )

  def playerView(metadata: TorrentMetadataReceived, index: Int) =
    val file = metadata.files(index)
    val fileName = file.path.last
    val videoIndices = metadata.files.zipWithIndex.collect { case (f, i) if isVideoFile(f.path.last) => i }
    val pos = videoIndices.indexOf(index)
    val prev = if pos > 0 then Some(videoIndices(pos - 1)) else None
    val next = if pos >= 0 && pos < videoIndices.size - 1 then Some(videoIndices(pos + 1)) else None
    def navButton(label: String, icon: ReactiveSvgElement[org.scalajs.dom.SVGSVGElement], target: Option[Int]) =
      button(cls := "button is-light",
        disabled := target.isEmpty,
        onClick.mapTo(target).filter(_.isDefined).map(i => Page.Torrent(magnetLink.toMagnetUri, i)) --> navigateObserver,
        span(cls := "icon", icon),
        span(label)
      )
    div(
      onMountCallback(_ => loadingVar.set(false)),
      div(cls := "toolbar-row mb-4",
        button(cls := "button is-light",
          onClick.mapTo(Page.Torrent(magnetLink.toMagnetUri, None)) --> navigateObserver,
          span(cls := "icon", Icons.chevronLeft),
          span("All files")
        ),
        div(cls := "buttons",
          a(cls := "button is-light",
            href := videoUrl(index),
            target := "_blank",
            download := fileName,
            span(cls := "icon", Icons.download),
            span("Download")
          ),
          button(cls := "button is-light",
            cls.toggle("is-success") <-- copiedVar.signal.map(_.contains("player")),
            onClick.mapTo(("player", videoUrl(index))) --> copyTextObserver,
            span(cls := "icon", Icons.clipboard),
            span(child <-- copiedVar.signal.map(c => if c.contains("player") then "Copied!" else "Copy URL"))
          )
        )
      ),
      h4(cls := "title is-4 player-title", fileName),
      VideoPlayerComponent(fileName, videoUrl(index)),
      div(cls := "toolbar-row mt-4",
        navButton("Previous", Icons.chevronLeft, prev),
        navButton("Next", Icons.chevronRight, next)
      )
    )

  val content =
    torrentMetadataVar.signal.combineWith(torrentErrorVar.signal).map { (metadata, error) =>
      val notification = error.map(errorNotification).toList
      val body = (fileIndex, metadata) match {
        case (Some(i), Some(md)) if md.files.indices.contains(i) && isVideoFile(md.files(i).path.last) =>
          List(playerView(md, i))
        case (_, Some(md)) =>
          List(mainContent(md))
        case (_, None) =>
          if error.isEmpty then List(loadingSkeleton) else Nil
      }
      notification ++ body
    }

  div(
    child <-- loadingVar.signal.map {
      case true =>
        progressTag(cls := "progress is-primary", styleAttr := "height: 0.25rem; margin-bottom: -0.25rem")
      case false =>
        div()
    },
    sectionTag(cls := "section",
      div(cls := "container",
        onMountCallback { ctx =>
          import ctx.owner
          events.foreach {
            case e: TorrentStats => torrentStatsVar.set(Some(e))
            case e: TorrentMetadataReceived =>
              torrentMetadataVar.set(Some(e))
              metadataCache.update(_.updated(e.infoHash, e))
            case e: TorrentError => torrentErrorVar.set(Some(e.message))
          }
        },
        children <-- content,
        child <-- showModalVar.signal.map {
          case Some(file) => openModal(file, copyTextObserver, copiedVar.signal, showModalVar.toObserver.contramap(_ => None))
          case None => div()
        }
      )
    )
  )
end TorrentPageComponent

private case class ActiveFile(name: String, src: String)

private val controls = htmlProp[String, String]("controls", StringAsIsCodec)
private val download = htmlProp[String, String]("download", StringAsIsCodec)
private val valueAttr = htmlAttr[String]("value", StringAsIsCodec)
private val maxAttr = htmlAttr[String]("max", StringAsIsCodec)

private def isVideoFile(name: String) =
  name.split('.').lastOption.map(_.toLowerCase).exists(Set("mp4", "avi", "webm", "mkv", "mov", "m4v"))

private def fileIcon(name: String) =
  name.split('.').lastOption.map(_.toLowerCase) match
    case Some("mp4" | "avi" | "webm" | "mkv" | "mov" | "m4v") => Icons.play
    case Some("jpeg" | "jpg" | "png" | "gif" | "webp" | "svg" | "bmp") => Icons.image
    case _ => Icons.file

private def openModal(
  activeFile: ActiveFile,
  copyText: Observer[(String, String)],
  copied: Signal[Option[String]],
  close: Observer[Any]
) = {
  val ext = activeFile.name.split('.').lastOption.map(_.toLowerCase)
  ext match {
    case Some(_) if isVideoFile(activeFile.name) => videoModal(activeFile, copyText, copied, close)
    case Some("jpeg" | "jpg" | "png" | "gif" | "webp" | "bmp") => imageModal(activeFile, copyText, copied, close)
    case _ => genericModal(activeFile, copyText, copied, close)
  }
}

private def modalActions(activeFile: ActiveFile, copyText: Observer[(String, String)], copied: Signal[Option[String]]) =
  div(cls := "card-footer",
    a(cls := "card-footer-item",
      href := activeFile.src,
      target := "_blank",
      download := activeFile.name,
      span(cls := "icon", Icons.download),
      span("Download")
    ),
    a(cls := "card-footer-item",
      cls.toggle("has-text-success") <-- copied.map(_.contains("modal")),
      onClick.mapTo(("modal", activeFile.src)) --> copyText,
      span(cls := "icon", Icons.clipboard),
      span(child <-- copied.map(c => if c.contains("modal") then "Copied!" else "Copy URL"))
    )
  )

private def genericModal(
  activeFile: ActiveFile,
  copyText: Observer[(String, String)],
  copied: Signal[Option[String]],
  close: Observer[Any]
) = {
  modal(
    div(cls := "card",
      div(cls := "card-content",
        div(cls := "media",
          div(cls := "media-content",
            h4(cls := "title is-4", activeFile.name)
          )
        )
      ),
      modalActions(activeFile, copyText, copied)
    ),
    close
  )
}

private def videoModal(
  activeFile: ActiveFile,
  copyText: Observer[(String, String)],
  copied: Signal[Option[String]],
  close: Observer[Any]
) = {
  modal(
    div(cls := "card",
      div(cls := "card-image",
        VideoPlayerComponent(activeFile.name, activeFile.src)
      ),
      div(cls := "card-content",
        div(cls := "media",
          div(cls := "media-content",
            h4(cls := "title is-4", activeFile.name)
          )
        )
      ),
      modalActions(activeFile, copyText, copied)
    ),
    close
  )
}

private def imageModal(
  activeFile: ActiveFile,
  copyText: Observer[(String, String)],
  copied: Signal[Option[String]],
  close: Observer[Any]
) = {
  modal(
    div(cls := "card",
      div(cls := "card-image",
        figure(cls := "image is-4by3",
          img(src := activeFile.src)
        ),
      ),
      div(cls := "card-content",
        div(cls := "media",
          div(cls := "media-content",
            h4(cls := "title is-4", activeFile.name)
          )
        )
      ),
      modalActions(activeFile, copyText, copied)
    ),
    close
  )
}

private def modal(content: ReactiveHtmlElement[org.scalajs.dom.html.Div], close: Observer[Any]) = {
  div(cls := "modal is-active",
    onMountCallback { ctx =>
      import ctx.owner
      dom.document.body.classList.add("is-clipped")
      windowEvents(_.onKeyDown).filter(_.key == "Escape") --> close
    },
    onUnmountCallback(_ => dom.document.body.classList.remove("is-clipped")),
    div(cls := "modal-background",
      onClick --> close
    ),
    div(cls := "modal-content", content),
    button(cls := "modal-close is-large", aria.label := "close",
      onClick --> close
    )
  )
}

private def renderBytes(bytes: Long) =
  InformationFormatter.inBestUnit(Bytes(bytes)).rounded(1).toString

private class Switch[A](default: A)(using CanEqual[A, A]) {
  private val current: Var[A] = Var(default)
  def active: Signal[A] = current.signal
  def activate: Observer[A] = current.toObserver
  def port(name: String): Signal[Boolean] = current.signal.map(_ == name)
}

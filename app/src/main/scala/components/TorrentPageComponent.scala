package components

import org.scalajs.dom
import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Message.{TorrentMetadataReceived, TorrentStats, RequestTorrent}
import com.github.lavrov.bittorrent.app.protocol.{Message, Command, Event}
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import squants.information.Bytes
import util.{Config, InformationFormatter, MagnetLink}


def TorrentPageComponent(
  magnetLink: MagnetLink,
  send: Observer[RequestTorrent],
  events: EventStream[TorrentMetadataReceived | TorrentStats]
) =
  val infoHash = magnetLink.infoHash

  val currentTab = Switch["files" | "magnet"]("files")

  val showModalVar = Var(Option.empty[ActiveFile])
  val loadingVar = Var(true)
  val torrentMetadataVar = Var(Option.empty[TorrentMetadataReceived])
  val torrentStatsVar = Var(Option.empty[TorrentStats])

  val connectedPeerCount = torrentStatsVar.signal.map(_.map(_.connected).getOrElse(0))
  def availability(index: Int) =
    torrentStatsVar.signal
      .map {
        case Some(stats) => stats.availability.lift(index).getOrElse(0.0)
        case None => 0.0
      }
      .map(_ * 100)
      .map(_.toInt)
      .map(p => span(s"$p%"))

  def videoUrl(index: Int) =
    Config.httpUrl(s"torrent/$infoHash/data/$index")

  val onCopyClick = Observer[Int](fileIndex =>
    dom.window.navigator.clipboard.writeText(videoUrl(fileIndex))
  )

  def filesTab(files: List[Message.File]) =
    div(
      files.zipWithIndex.map { case (file, index) =>
        val fileName = file.path.last
        div(cls := "media",
          div(cls := "media-content",
            p(cls := "subtitle is-6",
              a(
                file.path.mkString,
                onClick.mapTo(ActiveFile(fileName, videoUrl(index))).map(Some(_)) --> showModalVar,
              )
            ),
          ),
          div(cls := "media-right",
            div(cls := "level level-right",
              p(cls := "level-item",
                span(cls := "is-size-7", renderBytes(file.size), " / ", child <-- availability(index))
              ),
              p(cls := "level-item",
                a(cls := "button is-small is-light",
                  href := videoUrl(index),
                  target := "_blank",
                  download := fileName,
                  "Download"
                )
              ),
              p(cls := "level-item",
                button(cls := "button is-small is-light",
                  onClick.mapTo(index) --> onCopyClick,
                  "Copy URL"
                )
              )
            )
          )
        )
      }
    )

  def magnetTab =
    div(cls := "is-clipped",
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
        div(cls := "column", magnetLink.trackers.map(p(_))),
      ),
    )

  val content =
    torrentMetadataVar
      .signal
      .map {
        case None =>
          List(
            div("Fetching torrent metadata")
          )
        case Some(metadata) =>
          List(
            h4(cls := "title is-4",
              onMountCallback(_ => loadingVar.set(false)),
              metadata.name
            ),
            navTag(cls := "level is-mobile",
              div(cls := "level-left",
                div(cls := "level-item has-text-centered mr-6",
                  div(
                    p(cls := "heading", "Files"),
                    p(cls := "subtitle", metadata.files.size)
                  )
                ),
                div(cls := "level-item has-text-centered  mr-6",
                  div(
                    p(cls := "heading", "Size"),
                    p(cls := "subtitle", renderBytes(metadata.files.map(_.size).sum))
                  )
                ),
                div(cls := "level-item has-text-centered",
                  div(
                    p(cls := "heading", "Peers"),
                    p(cls := "subtitle", child <-- connectedPeerCount.map(_.toString))
                  )
                )
              ),
              div(cls := "level-right")
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
            case e: TorrentMetadataReceived => torrentMetadataVar.set(Some(e))
          }
        },
        children <-- content,
        child <-- showModalVar.signal.map {
          case Some(file) => openModal(file, showModalVar.toObserver.contramap(_ => None))
          case None => div()
        }
      )
    )
  )
end TorrentPageComponent

private case class ActiveFile(name: String, src: String)

private val controls = htmlProp[String, String]("controls", StringAsIsCodec)
private val download = htmlProp[String, String]("download", StringAsIsCodec)

private def openModal(activeFile: ActiveFile, close: Observer[Any]) = {
  val ext = activeFile.name.split('.').lastOption
  ext match {
    case Some("mp4" | "avi" | "webm" | "mkv") => videoModal(activeFile, close)
    case Some("jpeg" | "jpg" | "png" | "gif") => imageModal(activeFile, close)
    case _ => genericModal(activeFile, close)
  }
}

private def genericModal(activeFile: ActiveFile, close: Observer[Any]) = {
  modal(
    div(cls := "card",
      div(cls := "card-content",
        div(cls := "media",
          div(cls := "media-content",
            h4(cls := "title is-4", activeFile.name)
          )
        )
      )
    ),
    close
  )
}

private def videoModal(activeFile: ActiveFile, close: Observer[Any]) = {
  modal(
    div(cls := "card",
      div(cls := "card-image",
        videoTag(cls := "is-4by3",
          width := "100%",
          controls := "true",
          sourceTag(
            src := activeFile.src
          )
        )
      ),
      div(cls := "card-content",
        div(cls := "media",
          div(cls := "media-content",
            h4(cls := "title is-4", activeFile.name)
          )
        )
      )
    ),
    close
  )
}

private def imageModal(activeFile: ActiveFile, close: Observer[Any]) = {
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
      )
    ),
    close
  )
}

private def modal(content: ReactiveHtmlElement[org.scalajs.dom.html.Div], close: Observer[Any]) = {
  div(cls := "modal is-active",
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
package pages

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import squants.experimental.formatter.Formatters.InformationMetricFormatter
import squants.information.Bytes
import util.MagnetLink


def TorrentPage(
  magnetLink: MagnetLink,
  send: Observer[Command.GetTorrent],
  events: EventStream[Event.TorrentMetadataReceived]
) =
  val infoHash = magnetLink.infoHash

  val showModalVar = Var(Option.empty[ActiveFile])
  val loadingVar = Var(true)

  def videoUrl(index: Int) =
    s"https://bittorrent-server.herokuapp.com/torrent/$infoHash/data/$index"

  val content =
    events
      .filter(_.infoHash == infoHash)
      .startWithNone
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
            nav(cls := "level is-mobile",
              div(cls := "level-left",
                div(cls := "level-item has-text-centered mr-6",
                  div(
                    p(cls := "heading", "Files"),
                    p(cls := "subtitle", metadata.files.size)
                  )
                ),
                div(cls := "level-item has-text-centered",
                  div(
                    p(cls := "heading", "Size"),
                    p(cls := "subtitle", renderBytes(metadata.files.map(_.size).sum))
                  )
                )
              ),
              div(cls := "level-right")
            ),
            div(cls := "tabs",
              ul(
                li(cls := "is-active", a("Files")),
              )
            ),
            div(
              metadata.files.zipWithIndex.map { case (file, index) =>
                div(cls := "media",
                  div(cls := "media-content",
                    p(cls := "subtitle is-6",
                      a(
                        file.path.mkString,
                        onClick.mapTo(ActiveFile(file.path.last, videoUrl(index))).map(Some(_)) --> showModalVar,
                      )
                    ),
                  ),
                  div(cls := "media-right",
                    p(cls := "buttons",
                      a(cls := "button is-small is-light",
                        href := videoUrl(index),
                        download := "",
                        "Download"
                      )
                    )
                  )
                )
              }
            )
          )
      }

  div(
    child <-- loadingVar.signal.map {
      case true =>
        progress(cls := "progress is-primary", styleAttr := "height: 0.25rem; margin-bottom: -0.25rem")
      case false =>
        div()
    },
    section(cls := "section",
      div(cls := "container",
        onMountCallback(_ => send.onNext(Command.GetTorrent(infoHash, magnetLink.trackers))),
        children <-- content,
        child <-- showModalVar.signal.map {
          case Some(file) => openModal(file, showModalVar.toObserver.contramap(_ => None))
          case None => div()
        }
      )
    )
  )
end TorrentPage

private case class ActiveFile(name: String, src: String)

private val controls = customProp("controls", StringAsIsCodec)
private val download = customProp("download", StringAsIsCodec)

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
      div(cls:="card-content",
        div(cls:="media",
          div(cls:="media-content",
            h4(cls:="title is-4", activeFile.name)
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
        video(cls := "is-4by3",
          width := "100%",
          controls := "true",
          source(
            src := activeFile.src
          )
        )
      ),
      div(cls:="card-content",
        div(cls:="media",
          div(cls:="media-content",
            h4(cls:="title is-4", activeFile.name)
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
      div(cls:="card-content",
        div(cls:="media",
          div(cls:="media-content",
            h4(cls:="title is-4", activeFile.name)
          )
        )
      )
    ),
    close
  )
}

private def modal(content: ReactiveHtmlElement[org.scalajs.dom.html.Div], close: Observer[Any]) = {
  div(cls:="modal is-active",
    div(cls:="modal-background",
      onClick --> close
    ),
    div(cls:="modal-content", content),
    button(cls:="modal-close is-large", aria.label:="close",
      onClick --> close
    )
  )
}

private def renderBytes(bytes: Long) =
  InformationMetricFormatter.inBestUnit(Bytes(bytes)).rounded(1).toString

package default

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

import scala.util.chaining.scalaUtilChainingOps


object TorrentPage {

  def apply(infoHash: InfoHash, send: Observer[Command.GetTorrent], events: EventStream[Event.TorrentMetadataReceived]) = {

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
                      p(cls := "buttons mb-0",
                        a(cls := "button is-small is-light",
                          target := "_blank",
                          href := videoUrl(index),
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
          onMountCallback(_ => send.onNext(Command.GetTorrent(infoHash))),
          children <-- content,
          child <-- showModalVar.signal.map {
            case Some(file) => openModal(file, showModalVar.toObserver.contramap(_ => None))
            case None => div()
          }
        )
      )
    )
  }

  private case class ActiveFile(name: String, src: String)

  private val controls = customProp("controls", StringAsIsCodec)

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
}

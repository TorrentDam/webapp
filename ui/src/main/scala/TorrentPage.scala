package default

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.api.L._
import scala.util.chaining.scalaUtilChainingOps


object TorrentPage {

  def apply(infoHash: InfoHash, send: Observer[Command.GetTorrent], events: EventStream[Event.TorrentMetadataReceived]) = {

    val showModalVar = Var(Option.empty[ActiveFile])

    def videoUrl(index: Int) =
      s"https://bittorrent-server.herokuapp.com/torrent/$infoHash/data/$index"

    def isPlayable(src: String) = {
      val ext = src.split('.').lastOption
      ext match {
        case Some("mp4" | "avi" | "webm" | "mkv") => true
        case _ => false
      }
    }

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
              h4(cls := "title is-4", metadata.name),
              div(cls := "tabs",
                ul(
                  li(cls := "is-active", a("Files")),
                )
              ),
              div(
                metadata.files.zipWithIndex.map { case (file, index) =>
                  div(cls := "media",
                    div(cls := "media-content",
                      p(cls := "block", file.path.mkString),
                      div(cls := "buttons mb-0",
                        a(cls := "button is-small",
                          target := "_blank",
                          href := videoUrl(index),
                          "Download"
                        ),
                        isPlayable(file.path.last).pipe {
                          case true =>
                            button(cls := "button is-small",
                              onClick.mapTo(ActiveFile(file.path.last, videoUrl(index))).map(Some(_)) --> showModalVar,
                              "Watch"
                            )
                          case _ =>
                            div()
                        }
                      )
                    )
                  )
                }
              )
            )
        }

    div(
      onMountCallback(_ => send.onNext(Command.GetTorrent(infoHash))),
      children <-- content,
      child <-- showModalVar.signal.map {
        case Some(file) => videoModal(file, showModalVar.toObserver.contramap(_ => None))
        case None => div()
      }
    )
  }

  private case class ActiveFile(name: String, src: String)

  private val controls = customProp("controls", StringAsIsCodec)

  private def videoModal(activeFile: ActiveFile, close: Observer[Any]) = {
    div(cls:="modal is-active",
      div(cls:="modal-background",
        onClick --> close
      ),
      div(cls:="modal-content",
        div(cls := "card",
          div(cls := "card-image",
            video(cls := "is-4by3",
              width := "100%",
              controls := "true",
              source(
                src := activeFile.src
              )
            ),
            div(cls:="card-content",
              div(cls:="media",
                div(cls:="media-content",
                  h4(cls:="title is-4", activeFile.name)
                )
              )
            )
          )
        )
      ),
      button(cls:="modal-close is-large", aria.label:="close",
        onClick --> close
      )
    )
  }
}

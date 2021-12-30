package pages

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Event
import com.raquo.laminar.api.L._
import default.Routing
import util.MagnetLink
import scodec.bits.ByteVector

import scala.concurrent.ExecutionContext.Implicits.global


def SearchPage(
  query: Signal[Option[String]],
) =
  section(cls := "section",
    onMountInsert { ctx =>
      val searchTermVar = Var(initial = "")
      val infoHashSignal =
        searchTermVar
          .signal
          .map {
            case InfoHash.fromString(infoHash) => Some(infoHash)
            case MagnetLink.fromString.unlift(link) => Some(link.infoHash)
            case _ => None
          }
          .observe(ctx.owner)
      div(
        cls := "container is-max-desktop",
        form(cls := "block",
          onSubmit.preventDefault --> { _ =>
            infoHashSignal.now() match
              case Some(infoHash) =>
                Routing.router.pushState(Routing.Page.Torrent(infoHash))
              case _ =>
          },
          div(cls := "field has-addons",
            div(cls := "control is-large is-expanded",
              input(
                cls := "input is-primary is-large",
                typ := "text",
                placeholder := "magnet:",
                value <-- query.map(_.getOrElse("")),
                onInput.mapToValue --> searchTermVar
              )
            ),
            child <-- infoHashSignal.map {
              case Some(_) =>
                div(cls := "control is-hidden-mobile",
                  button(cls := "button is-primary is-large", "Open")
                )
              case None =>
                emptyNode
            }
          )
        ),
      )
    },
  )
end SearchPage
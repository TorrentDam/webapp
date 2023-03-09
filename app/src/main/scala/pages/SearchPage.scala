package pages

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Event
import com.raquo.laminar.api.L.*
import default.Routing
import util.MagnetLink
import scodec.bits.ByteVector

import scala.concurrent.ExecutionContext.Implicits.global


def SearchPage(
  query: Signal[Option[String]],
) =
  sectionTag(cls := "section",
    onMountInsert { ctx =>
      val searchTermVar = Var(initial = "")
      searchTermVar
        .signal
        .foreach {
          case InfoHash.fromString(infoHash) =>
            Routing.router.pushState(Routing.Page.Torrent(s"magnet:?xt=urn:btih:$infoHash"))
          case url @ MagnetLink.fromString.unlift(_) =>
            Routing.router.pushState(Routing.Page.Torrent(url))
          case _ =>
        }(ctx.owner)
      div(
        cls := "container is-max-desktop",
        form(cls := "block",
          div(cls := "field",
            div(cls := "control is-large is-expanded",
              input(
                cls := "input is-primary is-large",
                typ := "text",
                placeholder := "magnet:",
                value <-- query.map(_.getOrElse("")),
                onInput.mapToValue --> searchTermVar
              )
            )
          )
        ),
      )
    },
  )
end SearchPage
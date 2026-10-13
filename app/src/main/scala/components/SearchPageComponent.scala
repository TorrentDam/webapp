package components

import com.github.lavrov.bittorrent.app.protocol.{Event, InfoHash}
import com.raquo.laminar.api.L.*
import util.MagnetLink
import scodec.bits.ByteVector

import scala.concurrent.ExecutionContext.Implicits.global


def SearchPageComponent(
  query: Signal[Option[String]],
) =
  sectionTag(cls := "section",
    onMountInsert { ctx =>
      val searchTermVar = Var(initial = "")
      searchTermVar
        .signal
        .foreach { searchTerm =>
          if InfoHash.fromString(searchTerm).isDefined then
            routing.router.pushState(routing.Page.Torrent(s"magnet:?xt=urn:btih:$searchTerm"))
          else if MagnetLink.fromString(searchTerm).isDefined then
            routing.router.pushState(routing.Page.Torrent(searchTerm))
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
end SearchPageComponent
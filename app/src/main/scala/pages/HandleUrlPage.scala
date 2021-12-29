package pages

import com.raquo.laminar.api.L.*
import default.Routing
import util.MagnetLink


def HandleUrlPage(url: String) =
  section(cls := "section",
    div(cls := "container",
      MagnetLink.fromString(url) match
        case Some(link) =>
          div(
            onMountCallback(ctx =>
              import ctx.owner
              EventStream
                .fromValue((), emitOnce = true)
                .delay(100)
                .foreach(_ =>
                  Routing.router.pushState(Routing.Page.Torrent(link.infoHash))
                )
            ),
            s"Handling magnet link..."
          )
        case None =>
          div("Invalid link")
    )
  )

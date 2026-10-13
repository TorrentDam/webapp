package components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import util.Config

def RootComponent(connected: Signal[Boolean], modifiers: Modifier[Div]*) =
  div(
    navTag(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := Config.relativeUrl("/"),
            img(className := "logo", src := Config.relativeUrl("images/windmill.svg")),
            span(className := "title", "TorrentDam")
          ),
          div(className := "navbar-item",
            span(className := "tags has-addons",
              className.toggle("is-hidden") <-- EventStream.fromValue(false).delay(1000).startWith(true),
              span(className := "tag", "connected"),
              span(className := "tag",
                className <-- connected.map(if _ then "is-success" else "is-danger"),
                child <-- connected.map(if _ then "yes" else "no")
              )
            )
          )
        )
      )
    )
  ).amend(modifiers)

enum IndexStatus {
  case NotSupported
  case Supported(ready: Signal[Boolean])
}

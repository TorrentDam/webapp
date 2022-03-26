package default


import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def App(modifiers: Modifier[Div]*) =
  div(
    nav(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := "/",
            img(className := "logo", src := "/images/windmill.svg"),
            span(className := "title", "TorrentDam")
          )
        )
      )
    )
  ).amend(modifiers)

enum IndexStatus {
  case NotSupported
  case Supported(ready: Signal[Boolean])
}

package default


import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def App(connected: Signal[Boolean], modifiers: Modifier[Div]*) =
  div(
    nav(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := "/",
            img(className := "logo", src := "/images/windmill.svg"),
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

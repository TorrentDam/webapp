package default


import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def App(isIndexReady: Signal[Boolean], modifiers: Modifier[Div]*) =
  div(
    nav(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := "https://torrentdam.github.io/",
            img(className := "logo", src := "/images/windmill.svg"),
            span(className := "title", "TorrentDam")
          )
        ),
        div(className := "navbar-end",
          div(className := "navbar-item",
          child <-- isIndexReady.map {
              case true =>
                span(className := "icon has-text-success", i(className := "fas fa-check"))
              case false =>
                span(className := "icon has-text-info index-refresh-icon", i(className := "fas fa-arrow-down"))
            }
          )
        )
      )
    )
  ).amend(modifiers)

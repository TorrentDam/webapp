package default


import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def App(isRefreshing: Signal[Boolean], modifiers: Modifier[Div]*) =
  div(
    nav(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := "https://torrentdam.github.io/",
            img(src := "/images/windmill.svg", width := "28", height := "48"),
            span(className := "title", "TorrentDam")
          )
        )
      ),
      div(className := "navbar-end",
        child <-- isRefreshing.map {
          case true =>
            emptyNode
          case false =>
            div(className := "navbar-item",
              "Downloading index"
            )
        }
      )
    )
  ).amend(modifiers)

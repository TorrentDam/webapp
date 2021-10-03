package default


import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def App(indexStatus: IndexStatus, modifiers: Modifier[Div]*) =
  div(
    nav(className := "navbar has-shadow is-spaced",
      div(className := "container",
        div(className := "navbar-brand",
          a(className := "navbar-item", href := "https://torrentdam.github.io/",
            img(className := "logo", src := "/images/windmill.svg"),
            span(className := "title", "TorrentDam")
          )
        ),
        div(className := "navbar-menu",
          div(className := "navbar-end",
            div(className := "navbar-item",
              child <-- indexStatus.match {
                case IndexStatus.NotSupported =>
                  Signal.fromValue(emptyNode)
                case IndexStatus.Supported(ready) =>
                  ready.map {
                    case true =>
                      span(className := "icon has-text-success",
                        title := "Index is up to date",
                        i(className := "fas fa-check")
                      )
                    case false =>
                      span(className := "icon has-text-info index-refresh-icon",
                        title := "Downlaoding index",
                        i(className := "fas fa-arrow-down")
                      )
                  }
              }
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

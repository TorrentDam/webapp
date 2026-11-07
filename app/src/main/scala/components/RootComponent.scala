package components

import com.raquo.laminar.api.L.*
import util.{Config, Theme}

def RootComponent(connected: Signal[Boolean], reconnect: Observer[Any], modifiers: Modifier[Div]*) =
  div(cls := "app-root",
    navTag(cls := "navbar has-shadow", role := "navigation", aria.label := "Main navigation",
      div(cls := "container",
        div(cls := "navbar-brand",
          a(cls := "navbar-item", href := Config.relativeUrl("/"),
            img(cls := "logo", src := Config.relativeUrl("images/windmill.svg"), alt := "TorrentDam logo"),
            span(cls := "title mb-0", "TorrentDam")
          )
        ),
        div(cls := "navbar-actions",
            a(cls := "connection-pill",
              role := "button",
              aria.label := "Connection status, click to reconnect",
              title := "Click to reconnect",
              onClick --> reconnect,
              span(cls := "connection-dot",
                cls.toggle("is-online") <-- connected,
                cls.toggle("is-offline") <-- connected.map(!_)
              ),
              span(cls := "connection-label is-hidden-mobile",
                child <-- connected.map(if _ then "Connected" else "Disconnected")
              )
            ),
            button(cls := "button is-small is-ghost theme-toggle",
              aria.label := "Toggle dark mode",
              title := "Toggle dark mode",
              onClick --> Theme.toggle,
              span(cls := "icon",
                child <-- Theme.isDark.signal.map(if _ then Icons.sun else Icons.moon)
              )
            )
        )
      )
    ),
  ).amend(modifiers: _*).amend(
    footerTag(cls := "footer",
      div(cls := "content has-text-centered",
        p(
          strong("TorrentDam"),
          " — stream torrents straight from the swarm in your browser. ",
          a(href := "https://github.com/TorrentDam", target := "_blank", rel := "noopener", "Source on GitHub")
        )
      )
    )
  )

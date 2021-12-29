package pages

import com.raquo.laminar.api.L.*


def HandleUrlPage(url: String) =
  section(cls := "section",
    div(cls := "container",
      div(s"Handling magnet $url")
    )
  )

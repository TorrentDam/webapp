package pages

import com.raquo.laminar.api.L.*


def HandleMagnetPage(url: String) =
  section(cls := "section",
    div(cls := "container",
      div(s"Handling magnet $url")
    )
  )

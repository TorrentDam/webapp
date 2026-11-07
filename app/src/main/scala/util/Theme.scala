package util

import com.raquo.laminar.api.L.*
import org.scalajs.dom

object Theme {

  private val storageKey = "theme"
  private val lightColor = "#ffffff"
  private val darkColor = "#0f1114"

  private def stored: Option[String] =
    Option(dom.window.localStorage.getItem(storageKey)).filter(t => t == "light" || t == "dark")

  private def systemDark: Boolean =
    dom.window.matchMedia("(prefers-color-scheme: dark)").matches

  private def current: String = stored.getOrElse(if systemDark then "dark" else "light")

  val isDark: Var[Boolean] = Var(current == "dark")

  private def apply(theme: String): Unit = {
    dom.document.documentElement.setAttribute("data-theme", theme)
    val meta = dom.document.querySelector("meta[name=theme-color]")
    if meta != null then meta.setAttribute("content", if theme == "dark" then darkColor else lightColor)
  }

  val toggle: Observer[Any] = Observer[Any] { _ =>
    val next = if isDark.now() then "light" else "dark"
    dom.window.localStorage.setItem(storageKey, next)
    apply(next)
    isDark.set(next == "dark")
  }

  def init(): Unit = {
    apply(current)
    isDark.set(current == "dark")
    dom.window
      .matchMedia("(prefers-color-scheme: dark)")
      .addEventListener("change", (_: dom.Event) =>
        if stored.isEmpty then
          val theme = if systemDark then "dark" else "light"
          apply(theme)
          isDark.set(theme == "dark")
      )
  }
}

package components

import com.github.lavrov.bittorrent.app.protocol.InfoHash
import com.raquo.laminar.api.L.*
import org.scalajs.dom
import util.MagnetLink

import scala.concurrent.ExecutionContext.Implicits.global


def SearchPageComponent(
  query: Signal[Option[String]],
) =
  sectionTag(cls := "hero is-primary is-fullheight-with-navbar",
    onMountInsert { ctx =>
      import ctx.owner

      val searchTermVar = Var("")
      val invalidVar = Var(false)

      def isValid(term: String): Boolean =
        InfoHash.fromString(term).isDefined || MagnetLink.fromString(term).isDefined

      def navigate(term: String): Unit =
        if InfoHash.fromString(term).isDefined then
          routing.router.pushState(routing.Page.Torrent(s"magnet:?xt=urn:btih:$term"))
        else if MagnetLink.fromString(term).isDefined then
          routing.router.pushState(routing.Page.Torrent(term))

      query.foreach(q => searchTermVar.set(q.getOrElse("")))

      searchTermVar.signal.foreach { searchTerm =>
        val term = searchTerm.trim
        if term.nonEmpty && isValid(term) then
          invalidVar.set(false)
          navigate(term)
      }

      val submitObserver = Observer[String] { term =>
        val trimmed = term.trim
        if trimmed.isEmpty then invalidVar.set(false)
        else if isValid(trimmed) then
          invalidVar.set(false)
          navigate(trimmed)
        else invalidVar.set(true)
      }

      val pasteObserver = Observer[Any] { _ =>
        dom.window.navigator.clipboard.readText().toFuture.foreach { text =>
          val term = text.trim
          if term.nonEmpty then
            searchTermVar.set(term)
            if !isValid(term) then invalidVar.set(true)
        }
      }

      div(cls := "hero-body",
        div(cls := "container has-text-centered",
          h1(cls := "title is-2", "TorrentDam"),
          p(cls := "subtitle is-5 mb-6",
            "Paste a magnet link or info hash to stream torrents in your browser"),
          form(cls := "block",
            onSubmit.preventDefault.map(_ => searchTermVar.now()) --> submitObserver,
            div(cls := "field has-addons has-addons-centered search-form",
              div(cls := "control is-expanded has-icons-left",
                input(
                  cls := "input is-large",
                  cls.toggle("is-danger") <-- invalidVar.signal,
                  typ := "text",
                  placeholder := "magnet:?xt=urn:btih:…  or  a 40-char info hash",
                  aria.label := "Magnet link or info hash",
                  value <-- searchTermVar.signal,
                  onInput.mapToValue --> searchTermVar
                ),
                span(cls := "icon is-left", Icons.search)
              ),
              div(cls := "control",
                button(cls := "button is-primary is-large", typ := "submit", "Open")
              )
            ),
            p(cls := "help is-danger mt-2", cls.toggle("is-hidden") <-- invalidVar.signal.map(!_),
              "That doesn't look like a magnet link or an info hash"),
            div(cls := "mt-4",
              button(cls := "button is-light is-small", typ := "button",
                onClick --> pasteObserver,
                span(cls := "icon", Icons.clipboard),
                span("Paste from clipboard")
              )
            )
          )
        )
      )
    },
  )
end SearchPageComponent

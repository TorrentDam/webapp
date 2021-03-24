package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.core.{EventStream, Observer}
import com.raquo.laminar.api.L._


object Search {

  def apply(send: Observer[Command.Search], receive: EventStream[Event.SearchResults]) = {

    val searchTermVar = Var(initial = "")

    div(
      cls := "container is-max-desktop",
      form(cls := "block",
        onSubmit.preventDefault.mapTo(Command.Search(searchTermVar.now)) --> send,
        div(cls := "field has-addons",
          div(cls := "control is-expanded",
            input(
              cls := "input is-primary is-large",
              typ := "text",
              placeholder := "Search",
              controlled(
                value <-- searchTermVar,
                onInput.mapToValue --> searchTermVar
              )
            )
          ),
          div(cls := "control is-hidden-mobile",
            button(cls := "button is-primary is-large", "Go")
          )
        )
      ),
      ul(cls := "block",
        children <-- receive.map { results =>
          results.entries.map { entry =>
            li(cls := "box mb-1",
              entry.name
            )
          }
        }
      )
    )
  }
}

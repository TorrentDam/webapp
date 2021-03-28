package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.core.{EventStream, Observer}
import com.raquo.laminar.api.L._
import squants.experimental.formatter.Formatters.InformationMetricFormatter
import squants.information.{Bytes, Information}


object SearchPage {

  def apply(query: Option[String], send: Observer[Command.Search], receive: EventStream[Event.SearchResults]) = {

    val searchTermVar = Var(initial = query.getOrElse(""))

    div(
      cls := "container is-max-desktop",
      onMountCallback { _ =>
        query.foreach { v =>
          send.onNext(Command.Search(v))
        }
      },
      form(cls := "block",
        onSubmit.preventDefault --> { _ =>
          val value = PartialFunction.condOpt(searchTermVar.now()) {
            case str if str.nonEmpty => str
          }
          Routing.router.pushState(Routing.Page.Root(value))
          value.foreach { v =>
            send.onNext(Command.Search(v))
          }
        },
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
      children <-- receive.map { results =>
        results.entries.map { entry =>
          div(cls := "media",
            div(cls := "media-content",
              div(cls := "content",
                p(cls := "subtitle is-6",
                  a(
                    onClick --> {_ => Routing.router.pushState(Routing.Page.Torrent(entry.infoHash))},
                    entry.name
                  )
                ),
                p(cls := "level is-mobile",
                  div(cls := "level-left",
                    div(cls := "level-item",
                      span(cls := "is-size-7 has-text-weight-light",
                        InformationMetricFormatter.inBestUnit(Bytes(entry.size)).rounded(1).toString
                      )
                    ),
                    div(cls := "level-item",
                      span(cls := "tags m-0",
                        entry.ext.map( ext =>
                          span(cls := "tag is-light my-0", ext)
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        }
      }
    )
  }
}

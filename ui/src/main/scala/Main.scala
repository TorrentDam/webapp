import com.raquo.airstream.core.Sink
import org.scalajs.dom
import com.raquo.laminar.api.L._

object Main {

  def main(args: Array[String]): Unit = {

    val searchTermVar = Var(initial = "")

    val rootElement = div(
      cls := "container is-max-desktop",
      form(
        onSubmit.preventDefault --> { (_: Any) => println(searchTermVar.now()) },
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
          div(cls := "control",
            button(cls := "button is-primary is-large", "Go")
          )
        )
      )
    )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)
  }

}

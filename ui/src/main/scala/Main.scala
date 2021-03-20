import org.scalajs.dom
import com.raquo.laminar.api.L._

object Main {

  def main(args: Array[String]): Unit = {

    val nameVar = Var(initial = "world")

    val rootElement = div(
      className := "content",
      h1("Laminar"),
      label("Your name: "),
      input(
        onMountFocus,
        placeholder := "Enter your name here",
        inContext { thisNode => onInput.mapTo(thisNode.ref.value) --> nameVar }
      ),
      span(
        "Hello, ",
        child.text <-- nameVar.signal.map(_.reverse)
      )
    )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)
  }

}

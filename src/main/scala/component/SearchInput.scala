package component

import com.github.lavrov.bittorrent.InfoHash
import component.material_ui.core.{IconButton, InputBase, Paper}
import typings.materialUiIcons.{components => icons}
import logic.Dispatcher
import org.scalajs.dom.Event
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, ReactElement}
import slinky.web.html.{className, div, form, onSubmit}

@react
object SearchInput {

  case class Props(initialValue: String, formClass: String, inputClass: String, after: Option[ReactElement], dispatcher: Dispatcher)


  val component = FunctionalComponent[Props] { props =>
    val (state, setState) = Hooks.useState(props.initialValue)

    val infoHashOpt = extractInfoHash.lift(state)

    def handleSubmit(e: SyntheticEvent[org.scalajs.dom.html.Form, Event]): Unit = {
      e.preventDefault()
      infoHashOpt match {
        case Some(infoHash) =>
          Navigate(Routes.torrent(infoHash))
        case None =>
          Navigate(Routes.search(state))
      }
    }
    form(
      className := props.formClass,
      onSubmit := (handleSubmit(_)),
    )(
      InputBase(
        placeholder = "Search...",
        value = state,
        onChange = event => setState(event.target.value.toString),
        className = props.inputClass
      ),
      props.after
    )
  }


  private val regex = """xt=urn:btih:(\w+)""".r

  private val infoHashInUri: String => Option[InfoHash] =
    regex
      .findFirstMatchIn(_)
      .map(_.group(1))
      .flatMap(InfoHash.fromString.lift)

  private val extractInfoHash: PartialFunction[String, InfoHash] = {
    InfoHash.fromString.orElse(infoHashInUri.unlift)
  }
}

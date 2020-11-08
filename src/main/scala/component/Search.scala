package component

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Event.SearchResults
import logic.{Action, Dispatcher, State}
import material_ui.core._
import typings.materialUiIcons.{components => icons}
import material_ui.styles.makeStyles
import org.scalajs.dom.Event
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, React, ReactElement}
import slinky.web.html._
import squants.information

import scala.scalajs.js.Dynamic.{literal => obj}

@react
object Search {

  case class Props(query: String, model: Option[State.Search], discovered: Option[State.Discovered], dispatcher: Dispatcher)

  private val useStyles = makeStyles(theme =>
    obj(
      root = obj(
        padding = theme.spacing(1),
        display = "flex",
        textAlign = "center",
        marginTop = theme.spacing(4)
      ),
      input = obj(
        marginLeft = theme.spacing(1),
        marginRight = theme.spacing(1),
        flex = 1
      ),
      searchContent = obj(
        marginTop = theme.spacing(4)
      ),
      notFound = obj(
        textAlign = "center",
        marginTop = theme.spacing(4)
      )
    )
  )

  val component = FunctionalComponent[Props] { props =>
    val classes = useStyles()
    Hooks.useEffect(() =>
      if (!props.model.exists(_.query == props.query))
        props.dispatcher(Action.Search(props.query))
    )
    div(
      React.memo(SearchBox.component, (a: SearchBox.Props, b: SearchBox.Props) => a.initialValue != b.initialValue)(
        SearchBox.Props(props.query, props.dispatcher)
      ),
      div(className := classes.searchContent.toString)(
        props.model match {
          case Some(search) =>
            search.results.map { results =>
              val items = results.collect {
                case SearchResults.Entry(title, infoHash, size) =>
                  (infoHash, title, information.Bytes(size))
              }

              val element: ReactElement =
                if (items.nonEmpty)
                  Fade(in = true)(
                    TorrentList("Search results", items)
                  )
                else
                  p(className := classes.notFound.toString)("Nothing discovered yet")

              element
            }
          case _ =>
            div()
        }
      )
    )
  }

  @react
  object SearchBox {

    case class Props(initialValue: String, dispatcher: Dispatcher)

    val component = FunctionalComponent[Props] { props =>
      val classes = useStyles()
      val (state, setState) = Hooks.useState(props.initialValue)

      val infoHashOpt = extractInfoHash.lift(state)

      def handleSubmit(e: Event) = {
        e.preventDefault()
        infoHashOpt match {
          case Some(infoHash) =>
            Navigate(Routes.torrent(infoHash))
          case None =>
            Navigate(Routes.search(state))
        }
      }

      div(
        Paper(className = classes.root.toString, component = "form", onSubmit = handleSubmit _)(
          InputBase(
            placeholder = "Info hash or magnet link",
            value = state,
            onChange = event => setState(event.target.value.toString),
            className = classes.input.toString
          ),
          IconButton(`type` = "submit")(
            icons.ArrowForward()
          )
        )
      )
    }
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

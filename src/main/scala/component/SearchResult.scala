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
object SearchResult {

  case class Props(query: String, model: Option[State.Search], dispatcher: Dispatcher)

  private val useStyles = makeStyles(theme =>
    obj(
      root = obj(
        padding = theme.spacing(1),
        display = "flex",
        textAlign = "center"
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

}


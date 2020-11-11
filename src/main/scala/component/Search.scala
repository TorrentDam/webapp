package component

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Event.SearchResults
import logic.{Action, Dispatcher, State}
import material_ui.core._
import typings.materialUiIcons.{components => icons}
import material_ui.styles.makeStyles
import org.scalajs.dom.Event
import slinky.core.{FunctionalComponent, SyntheticEvent}
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, React}
import slinky.web.html._

import scala.scalajs.js.Dynamic.{literal => obj}

object Search {

  @react
  object SearchInAppBar {

    case class Props(initialValue: String,  dispatcher: Dispatcher)

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
        ),
        search = obj(
          position = "relative",
          borderRadius = theme.shape.borderRadius,
          //        backgroundColor = fade(theme.palette.common.white.toString, 0.15),
          backgroundColor = "rgba(255, 255, 255, 0.15)",
          marginRight = theme.spacing(2),
          marginLeft = 0,
          `[theme.breakpoints.up('sm')]` = obj(
            marginLeft = theme.spacing(3),
            width = "auto",
          ),
        ),
        searchIcon = obj(
          padding = theme.spacing(0, 2),
          height = "100%",
          position = "absolute",
          pointerEvents = "none",
          display = "flex",
          alignItems = "center",
          justifyContent = "center",
        ),
        searchInput = obj(
          padding = theme.spacing(1, 1, 1, 0),
          // vertical padding + font size from searchIcon
          paddingLeft = s"calc(1em + ${theme.spacing(4)}px)",
          transition= theme.transitions.create("width"),
          width = "100%",
          `[theme.breakpoints.up('md')]`= obj(
            width = "20ch",
          ),
          color = "inherit"
        )
      )
    )

    val component = FunctionalComponent[Props] { props =>
      val classes = useStyles()

      div(className := classes.search.toString)(
        div(className := classes.searchIcon.toString)(icons.Search()),
        SearchInput(props.initialValue, "", classes.searchInput.toString, None, props.dispatcher)
      )
    }
  }

  @react
  object SearchBox {

    case class Props(dispatcher: Dispatcher)

    private val useStyles = makeStyles(theme =>
      obj(
        root = obj(
          padding = theme.spacing(1),
          display = "flex",
          textAlign = "center"
        ),
        form = obj(
          display = "flex",
          flex = 1
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

      Paper(className = classes.root.toString)(
        SearchInput(
          "",
          classes.form.toString,
          classes.input.toString,
          Some(
            IconButton(`type` = "submit")(
              icons.Search()
            )
          ),
          props.dispatcher
        )
      )
    }
  }
}

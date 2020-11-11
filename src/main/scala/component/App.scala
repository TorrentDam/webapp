package component

import component.material_ui.core._
import component.material_ui.styles.makeStyles
import typings.materialUiIcons.{components => icons}
import logic.{Dispatcher, State}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => obj}
import scala.scalajs.js.annotation.JSImport

@react
object App {
  case class Props(model: State, dispatcher: Dispatcher)

  private val useStyles = makeStyles(theme =>
    obj(
      appBarSpacer = obj(
        marginBottom = theme.spacing(12)
      ),
      homeIcon = obj(
        marginRight = theme.spacing(2)
      ),
      appBarTitle = {
        val base =
          obj(
            display = "none",
          )
        base.updateDynamic(theme.breakpoints.up("sm").toString)(
          obj(display = "block")
        )
        base
      },
      flexGrow = obj(
        flexGrow = 1,
        display = "block",
      ),
      navLink = obj(
        margin = theme.spacing(1, 1.5)
      ),
      centered = obj(
        textAlign = "center"
      )
    )
  )

  @JSImport("windmill.svg", JSImport.Default)
  @js.native
  val windmillIcon: js.Object = js.native

  val component = FunctionalComponent[Props] { props =>
    val classes = useStyles()

    div()(
      CssBaseline(),
      div(className := classes.appBarSpacer.toString),
      AppBar(position = "fixed")(
        Container(maxWidth = "md")(
          Toolbar(disableGutters = true)(
            SvgIcon(
              className = classes.homeIcon.toString,
              component = windmillIcon,
              viewBox = "0 0 15 15",
              color = "inherit"
            ),
            Link(href = "#/", color = "inherit", className = classes.appBarTitle.toString)(
              Typography(variant = "h6")("TorrentDam")
            ),
            div(className := classes.flexGrow.toString),
            SwitchRoute
              .builder
              .route_(Routes.root)(div())
              .route(Routes.search)( query =>
                Search.SearchInAppBar(query, props.dispatcher),
              )
              .default(
                Search.SearchInAppBar("", props.dispatcher),
              ),
            IconButton(href = "https://github.com/TorrentDam/bittorrent")(
              icons.GitHub()
            )
          )
        )
      ),
      main(
        Container(maxWidth = "md")(
          if (props.model.connected) {
            SwitchRoute
              .builder
              .route_(Routes.root)(
                Search.SearchBox(props.dispatcher)
              )
              .route(Routes.search)(query =>
                SearchResult(query, props.model.search, props.dispatcher)
              )
              .route(Routes.torrent)( infoHash =>
                FetchingMetadata(
                  infoHash,
                  props.model.torrent,
                  props.dispatcher,
                  (torrent, metadata) => Torrent(torrent, metadata)
                )
              )
              .route(Routes.torrentFile){
                case (infoHash, index) =>
                  FetchingMetadata(
                    infoHash,
                    props.model.torrent,
                    props.dispatcher,
                    (_, metadata) =>
                      VideoPlayer(infoHash, metadata.files(index), index)
                   )
              }
              .default(div("Page not found"))
          } else
            p(className := classes.centered.toString)("Connecting to server...")
        )
      )
    )
  }
}

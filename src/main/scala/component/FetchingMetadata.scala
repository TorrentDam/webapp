package component

import com.github.lavrov.bittorrent.InfoHash
import component.material_ui.styles.makeStyles
import logic.{Action, Dispatcher, State}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, ReactElement}
import slinky.web.html.{className, p}

import scala.scalajs.js.Dynamic.literal

@react
object FetchingMetadata {

  case class Props(
    infoHash: InfoHash,
    torrent: Option[State.Torrent],
    dispatcher: Dispatcher,
    child: (State.Torrent, State.Metadata) => ReactElement
 )

  val component = FunctionalComponent[Props] { props =>
    val classes = useStyles()
    Hooks.useEffect( () =>
      if (!props.torrent.exists(_.infoHash == props.infoHash))
        props.dispatcher(Action.OpenTorrent(props.infoHash))
    )
    props.torrent match {
      case Some(torrent) =>
        torrent.metadata match {
          case None =>
            p(className := classes.centered.toString)(
              Connect(torrent.stats)( stats =>
                if (stats.connected == 0)
                  "Discovering peers"
                else
                  s"Fetching torrent metadata from ${stats.connected} peers"
              )
            )
          case Some(Left(_)) =>
            p(className := classes.centered.toString)("Could not fetch metadata")
          case Some(Right(metadata)) =>
            props.child(torrent, metadata)
        }

      case None =>
        p(className := classes.centered.toString)(
          "Opening torrent"
        )
    }
  }

  private val useStyles = makeStyles(_ =>
    literal(
      centered = literal(
        textAlign = "center"
      )
    )
  )
}

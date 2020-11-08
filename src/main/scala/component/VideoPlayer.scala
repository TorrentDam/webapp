package component

import com.github.lavrov.bittorrent.InfoHash
import typings.materialUiIcons.{components => icons}
import component.material_ui.core.Button
import typings.materialUiCore.components.{Card, CardActions, CardContent, CardMedia, Typography}
import logic.State
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.core.{CustomAttribute, FunctionalComponent}
import slinky.web.html._

import scala.scalajs.js

@react
object VideoPlayer {

  case class Props(infoHash: InfoHash, file: State.Metadata.File, index: Int)

  val component = FunctionalComponent[Props] { props =>
    val navigateBack: js.Function0[Unit] = () => Navigate(Routes.torrent(props.infoHash))
    val videoStreamUrl = environment.httpUrl(s"/torrent/${props.infoHash.toString}/data/${props.index}")
    div(
      Toolbar(disableGutters = true)(
        Button(startIcon = icons.ArrowBack(): ReactElement)(
          onClick := navigateBack,
          "Back to files"
        )
      ),

      Card()(
        CardMedia()(
          video(
            width := "100%",
            controls := true,
            new CustomAttribute[Boolean]("autoPlay") := true,
            source(
              src := videoStreamUrl
            )
          )
        ),
        CardContent()(
          Typography
            .set("variant", "h5")
            .set("component", "h2")(
              props.file.path.last
            )
            .set("noWrap", true),
        ),

        CardActions(
          Button(href = videoStreamUrl)("Download")
        )
      )
    )
  }
}

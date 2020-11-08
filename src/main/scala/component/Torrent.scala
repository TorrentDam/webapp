package component

import component.material_ui.core.{Divider, ListItem, ListItemText, List => MUIList}
import typings.materialUiCore.components.{Card, CardActions, CardContent, CardMedia, Typography}
import logic.State
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._
import squants.Percent
import squants.experimental.formatter.Formatters.InformationMetricFormatter

import scala.scalajs.js
import scala.math.Ordering.Implicits._

@react
object Torrent {
  case class Props(torrent: State.Torrent, metadata: State.Metadata)

  val component = FunctionalComponent[Props] { props =>

    def handlePlayClick(index: Int): js.Function0[Unit] =
      () => Navigate(Routes.torrentFile(props.torrent.infoHash, index))

    Card()(
      CardContent()(
        Typography
          .set("variant", "h5")
          .set("component", "h2")
          .set("gutterBottom", true)
          .set("noWrap", true)(
            props.metadata.name
          ),
        Divider(),
      ),
      Connect(props.torrent.stats)( stats =>
        renderList(props.metadata, stats.availability, handlePlayClick)
      )
    )
  }

  private def renderList(
    metadata: State.Metadata,
    availability: List[Double],
    handleClick: Int => () => Unit
  ): ReactElement =
    MUIList(
      for ((file, index) <- metadata.files.zipWithIndex.sortBy(_._1.path))
        yield {
          ListItem(button = true)(
            key := s"file-$index",
            onClick := handleClick(index),
            ListItemText(
              primary = Typography.set("noWrap", true)(file.path.last): ReactElement,
              secondary =
                InformationMetricFormatter.inBestUnit(file.size).rounded(1).toString() +
                availability
                  .lift(index)
                  .map { p =>
                    val percent = Percent(p * 100).rounded(1, BigDecimal.RoundingMode.FLOOR).toString()
                    s" | $percent"
                  }
                  .getOrElse("")
            )
          )
        }
    )

}

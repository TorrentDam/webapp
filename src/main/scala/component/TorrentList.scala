package component

import com.github.lavrov.bittorrent.InfoHash
import component.material_ui.core.CardActionArea
import component.material_ui.styles.makeStyles
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import squants.experimental.formatter.Formatters.InformationMetricFormatter
import typings.materialUiCore.components.{Card, CardContent, Typography, Chip}
import squants.information.Information

import scala.scalajs.js.Dynamic.{literal => obj}

@react
object TorrentList {

  case class Props(title: String, items: List[(InfoHash, String, Information, List[String])])

  val useStyles = makeStyles { theme =>
    obj(
      item = obj(
        marginBottom = theme.spacing(1)
      )
    )
  }

  val component = FunctionalComponent[Props] { props =>

    val classes = useStyles()

    def handleClick(infoHash: InfoHash) = () => Navigate(Routes.torrent(infoHash))

    for {
      ((infoHash, title, size, ext), index) <- props.items.zipWithIndex
    } yield {
      Card.className(classes.item.toString)(
        CardActionArea()(
          key := s"torrent-list-item-$index",
          onClick := handleClick(infoHash),
          CardContent()(
            Typography
              .set("gutterBottom", true)
              .set("variant", "h5")
              .set("component", "h2")(
                title
              )
              .set("noWrap", true),
            Typography
              .set("variant", "body2")
              .set("color", "textSecondary")
              .set("component", "p")(
                InformationMetricFormatter.inBestUnit(size).rounded(1).toString()
              ),
            Typography
              .set("variant", "body2")
              .set("color", "textSecondary")
              .set("component", "p")(
                ext.map { e =>
                  Chip
                    .set("label", e)
                    .set("size", "small")
                    .set("variant", "outlined")
                }
              )
          )
        )
      )
    }
  }
}

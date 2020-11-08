package component

import com.github.lavrov.bittorrent.InfoHash
import component.material_ui.core.CardActionArea
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.web.html._
import squants.experimental.formatter.Formatters.InformationMetricFormatter
import typings.materialUiCore.components.{Card, CardContent, Typography}
import squants.information.Information

@react
object TorrentList {

  case class Props(title: String, items: List[(InfoHash, String, Information)])

  val component = FunctionalComponent[Props] { props =>

    def handleClick(infoHash: InfoHash) = () => Navigate(Routes.torrent(infoHash))

    for {
      ((infoHash, title, size), index) <- props.items.zipWithIndex
    } yield {
      Card(
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
              )
          )
        )
      )
    }
  }
}

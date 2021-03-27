package default

import com.raquo.waypoint._
import com.raquo.laminar.api.L
import org.scalajs.dom
import upickle.default._


object Routing {

  sealed trait Page
  object Page {
    case object Root extends Page
    case class Torrent(infoHash: String) extends Page
  }

  implicit val torrentPageRW: ReadWriter[Page.Torrent] = macroRW
  implicit val pageRW: ReadWriter[Page] = macroRW

  val pathPrefix = root / "laminar-bulma"

  val router = new Router[Page](
    routes = List(
      Route.static(
        staticPage = Page.Root,
        pattern = pathPrefix / endOfSegments
      ),
      Route[Page.Torrent, String](
        _.infoHash,
        infoHash => Page.Torrent(infoHash),
        pattern = pathPrefix / "torrent" / segment[String] / endOfSegments
      )
    ),
    getPageTitle = _.toString,
    serializePage = page => write(page)(pageRW),
    deserializePage = pageStr => read(pageStr)(pageRW),
    routeFallback = _ => Page.Root
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner,
  )
}

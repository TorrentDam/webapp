package default

import com.github.lavrov.bittorrent.InfoHash
import com.raquo.waypoint._
import com.raquo.laminar.api.L
import upickle.default._
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}


object Routing {

  sealed trait Page
  object Page {
    case class Root(query: Option[String]) extends Page
    case class Torrent(infoHash: InfoHash) extends Page

    def toString(page: Page): String =
      page match
        case Root(query) => s"/?query=$query"
        case Torrent(infoHash) => s"/torrent/$infoHash"

    def fromString(string: String): Page =
      string match
        case s"/" => Root(None)
        case s"/?query=$query" => Root(Some(query))
        case s"/torrent/${InfoHash.fromString(infoHash)}" => Torrent(infoHash)
  }

  val appPathRoot = root

  implicit val infoHashFromString: FromString[InfoHash, DummyError] =
    FromString
      .factory(InfoHash.fromString.lift.andThen(_.toRight(DummyError.dummyError)))

  implicit val infoHashPrinter: Printer[InfoHash] = Printer.factory(_.toString)

  val router = new Router[Page](
    routes = List(
      Route.onlyQuery[Page.Root, List[String]](
        _.query.toList,
        query => Page.Root(query.headOption),
        pattern = (appPathRoot / endOfSegments) ? listParam[String]("q")
      ),
      Route[Page.Torrent, InfoHash](
        _.infoHash,
        infoHash => Page.Torrent(infoHash),
        pattern = appPathRoot / "torrent" / segment[InfoHash] / endOfSegments
      )
    ),
    getPageTitle = _.toString,
    serializePage = page => Page.toString(page),
    deserializePage = pageStr => Page.fromString(pageStr),
    routeFallback = _ => Page.Root(None),
    deserializeFallback = _ => Page.Root(None),
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner,
  )
}

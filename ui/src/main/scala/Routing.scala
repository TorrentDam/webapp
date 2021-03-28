package default

import com.github.lavrov.bittorrent.InfoHash
import com.raquo.waypoint._
import com.raquo.laminar.api.L
import upickle.default._
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}
import com.github.lavrov.bittorrent.app.protocol.CommonFormats.infoHashRW


object Routing {

  sealed trait Page
  object Page {
    case class Root(query: Option[String]) extends Page
    case class Torrent(infoHash: InfoHash) extends Page
  }

  implicit val rootPageRW: ReadWriter[Page.Root] = macroRW
  implicit val torrentPageRW: ReadWriter[Page.Torrent] = macroRW
  implicit val pageRW: ReadWriter[Page] = macroRW

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
    serializePage = page => write(page)(pageRW),
    deserializePage = pageStr => read(pageStr)(pageRW),
    routeFallback = _ => Page.Root(None)
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner,
  )
}

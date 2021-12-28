package default

import com.github.lavrov.bittorrent.InfoHash
import com.raquo.waypoint.*
import com.raquo.laminar.api.L
import upickle.default.*
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}


object Routing {

  enum Page:
    case Root(query: Option[String])
    case Torrent(infoHash: InfoHash)
    case HandleMagnet(url: String)

  object Page:

    def toString(page: Page): String =
      page match
        case Root(None) => "/"
        case Root(Some(query)) => s"/?query=$query"
        case Torrent(infoHash) => s"/torrent/$infoHash"
        case HandleMagnet(url) => s"/handle-magnet?url=$url"

    def fromString(string: String): Page =
      string match
        case s"/" => Root(None)
        case s"/?query=$query" => Root(Some(query))
        case s"/torrent/${InfoHash.fromString(infoHash)}" => Torrent(infoHash)
        case s"/handle-magnet?url=$url" => HandleMagnet(url)

  end Page

  val appPathRoot = root

  given FromString[InfoHash, DummyError] =
    FromString
      .factory(InfoHash.fromString.lift.andThen(_.toRight(DummyError.dummyError)))

  given Printer[InfoHash] = Printer.factory(_.toString)

  val router = Router[Page](
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
      ),
      Route.onlyQuery[Page.HandleMagnet, String](
        _.url,
        url => Page.HandleMagnet(url),
        pattern = (appPathRoot / "handle-magnet") ? param[String]("url")
      )
    ),
    getPageTitle = _ => "TorrentDam",
    serializePage = page => Page.toString(page),
    deserializePage = pageStr => Page.fromString(pageStr),
    routeFallback = _ => Page.Root(None),
    deserializeFallback = _ => Page.Root(None),
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner,
  )
}

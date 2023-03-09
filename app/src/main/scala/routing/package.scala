package routing

import com.github.lavrov.bittorrent.InfoHash
import com.raquo.laminar.api.{L, eventPropToProcessor}
import com.raquo.waypoint.*
import routing.Page
import routing.Page.{Root, Torrent}
import upickle.default.*
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}
import util.MagnetLink

enum Page {
  case Root(query: Option[String])
  case Torrent(url: String)
}

object Page {
  def toString(page: Page): String =
    page match
      case Root(None) => "/"
      case Root(Some(query)) => s"/?query=$query"
      case Torrent(url) => s"/torrent?url=$url"

  def fromString(string: String): Page =
    string match
      case s"/" => Root(None)
      case s"/?query=$query" => Root(Some(query))
      case s"/torrent?url=$url" => Torrent(url)
}

val appPathRoot = root

val router = Router[Page](
  routes = List(
    Route.onlyQuery[Page.Root, List[String]](
      _.query.toList,
      query => Page.Root(query.headOption),
      pattern = (appPathRoot / endOfSegments) ? listParam[String]("q")
    ),
    Route.onlyQuery[Page.Torrent, String](
      _.url,
      url => Page.Torrent(url),
      pattern = (appPathRoot / "torrent") ? param[String]("url")
    )
  ),
  getPageTitle = {
    case Page.Torrent(MagnetLink.fromString.unlift(MagnetLink(_, Some(displayName), _))) =>
      displayName + " - TorrentDam"
    case _ => "TorrentDam"
  },
  serializePage = page => Page.toString(page),
  deserializePage = pageStr => Page.fromString(pageStr),
  routeFallback = _ => Page.Root(None),
  deserializeFallback = _ => Page.Root(None),
)(
  popStateEvents = L.windowEvents(_.onPopState),
  owner = L.unsafeWindowOwner,
)

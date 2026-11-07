package routing

import com.github.lavrov.bittorrent.app.protocol.InfoHash
import com.raquo.laminar.api.{L, eventPropToProcessor}
import com.raquo.waypoint.*
import routing.Page
import routing.Page.{Root, Torrent}
import upickle.default.*
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}
import util.Config
import util.MagnetLink

import scala.util.Try

enum Page {
  case Root(query: Option[String])
  case Torrent(url: String, file: Option[Int] = None)
}

object Page {
  def toString(page: Page): String =
    page match
      case Root(None) => "/"
      case Root(Some(query)) => s"/?query=$query"
      case Torrent(url, None) => s"/torrent?url=$url"
      case Torrent(url, Some(file)) => s"/torrent?url=$url&file=$file"

  def fromString(string: String): Page =
    string match
      case s"/" => Root(None)
      case s"/?query=$query" => Root(Some(query))
      case s"/torrent?url=$url&file=$file" => Torrent(url, Try(file.toInt).toOption)
      case s"/torrent?url=$url" => Torrent(url, None)
}

val appPathRoot = root

val router = new Router[Page](
  routes = List(
    Route.onlyQuery[Page.Root, List[String]](
      _.query.toList,
      query => Page.Root(query.headOption),
      pattern = (appPathRoot / endOfSegments) ? listParam[String]("q"),
      basePath = Config.basePath
    ),
    Route.onlyQuery[Page.Torrent, (String, List[Int])](
      torrent => (torrent.url, torrent.file.toList),
      { case (url, file) => Page.Torrent(url, file.headOption) },
      pattern = (appPathRoot / "torrent") ? (param[String]("url") & listParam[Int]("file")),
      basePath = Config.basePath
    )
  ),
  getPageTitle = {
    case Page.Torrent(MagnetLink.fromString.unlift(MagnetLink(_, Some(displayName), _)), _) =>
      displayName + " - TorrentDam"
    case Page.Torrent(url, _) =>
      MagnetLink.fromString(url).map(m => s"${m.infoHash.toString.take(8)}… - TorrentDam").getOrElse("TorrentDam")
    case _ => "TorrentDam"
  },
  serializePage = page => Page.toString(page),
  deserializePage = pageStr => Page.fromString(pageStr),
  routeFallback = _ => Page.Root(None),
  deserializeFallback = _ => Page.Root(None),
  popStateEvents = L.windowEvents(_.onPopState),
  owner = L.unsafeWindowOwner,
)

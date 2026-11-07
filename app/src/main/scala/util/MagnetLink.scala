package util

import scala.scalajs.js.URIUtils
import com.github.lavrov.bittorrent.app.protocol.InfoHash

case class MagnetLink(infoHash: InfoHash, displayName: Option[String], trackers: List[String]) {
  def toMagnetUri: String = MagnetLink.toMagnetUri(this)
}

object MagnetLink {

  def toMagnetUri(link: MagnetLink): String = {
    val xt = s"xt=urn:btih:${link.infoHash}"
    val dn = link.displayName.map(name => s"dn=${URIUtils.encodeURIComponent(name)}").toList
    val tr = link.trackers.map(tracker => s"tr=${URIUtils.encodeURIComponent(tracker)}")
    (s"magnet:?" +: (xt :: dn ::: tr)).mkString("&")
  }

  def fromString(source: String): Option[MagnetLink] =
    source match
      case s"magnet:?$query" => fromQueryString(query)
      case _ => None

  private def fromQueryString(str: String) =
    val params = parseQueryString(str)
    for
      infoHash <- getInfoHash(params)
      displayName = getDisplayName(params)
      trackers = getTrackers(params)
    yield MagnetLink(infoHash, displayName, trackers)

  private type Query = Map[String, List[String]]

  private def getInfoHash(query: Query): Option[InfoHash] =
    query.get("xt").flatMap {
      case List(s"urn:btih:$value") => InfoHash.fromString(value)
      case _ => None
    }

  private def getDisplayName(query: Query): Option[String] =
    query.get("dn").flatMap(_.headOption)

  private def getTrackers(query: Query): List[String] =
    query.get("tr").toList.flatten

  private def parseQueryString(str: String): Query =
    str
      .split('&')
      .toList
      .map(URIUtils.decodeURIComponent)
      .map { p =>
        val split = p.split('=')
        (split.head, split.tail.mkString("="))
      }
      .groupMap(_._1)(_._2)

}

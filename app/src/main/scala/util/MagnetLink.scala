package util

import scala.scalajs.js.URIUtils
import com.github.lavrov.bittorrent.InfoHash

case class MagnetLink(infoHash: InfoHash, displayName: Option[String], trackers: List[String])

object MagnetLink {

  def fromString(source: String): Option[MagnetLink] =
    source match
      case s"magnet:?$query" => fromQueryString(query)

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
      case List(s"urn:btih:${InfoHash.fromString(ih)}") => Some(ih)
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

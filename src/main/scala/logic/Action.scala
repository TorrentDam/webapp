package logic

import com.github.lavrov.bittorrent.InfoHash

sealed trait Action

object Action {
  case class ServerEvent(payload: String) extends Action
  case class UpdateConnectionStatus(connected: Boolean) extends Action
  case class Search(query: String) extends Action
  case class OpenTorrent(infoHash: InfoHash) extends Action
}

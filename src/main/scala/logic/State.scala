package logic

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.Event.SearchResults
import squants.Quantity
import squants.information.Information
import trail.Path

case class State(
  connected: Boolean,
  path: Path,
  search: Option[State.Search],
  torrent: Option[State.Torrent],
  discovered: Option[State.Discovered],
  logs: List[String]
)

object State {

  def initial: State =
    State(
      connected = false,
      path = Path("/"),
      search = None,
      torrent = None,
      discovered = None,
      logs = List.empty
    )

  case class Search(
    query: String,
    results: Option[List[SearchResults.Entry]]
  )

  case class Torrent(
    infoHash: InfoHash,
    metadata: Option[Either[String, Metadata]],
    stats: Store[Torrent.Stats]
  ) {
    def withMetadata(metadata: Metadata): Torrent = copy(metadata = Some(Right(metadata)))
    def withError(message: String): Torrent = copy(metadata = Some(Left(message)))
  }
  object Torrent {
    case class Stats(
      connected: Int,
      availability: List[Double],
    )
  }

  case class Metadata(
    name: String,
    files: List[Metadata.File]
  )
  object Metadata {
    case class File(
      path: List[String],
      size: Quantity[Information]
    )
  }

  case class Discovered(torrents: List[(InfoHash, String)])
}


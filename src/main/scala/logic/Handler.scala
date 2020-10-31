package logic

import cats.implicits._
import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import logic.State.Torrent
import squants.information

trait Handler {

  def apply(model: State, action: Action): State
}

object Handler {

  def apply(send: Command => Unit): Handler =
    new Impl(send)

  private class Impl(send: Command => Unit) extends Handler {

    override def apply(model: State, action: Action): State = {
      action match {
        case Action.UpdateConnectionStatus(connected) =>
          model.copy(connected = connected)

        case Action.Search(query) =>
          model.search match {
            case Some(State.Search(`query`, _)) => model
            case _ =>
              send(Command.Search(query))
              val search = State.Search(query, None)
              model.copy(search = Some(search))
          }

        case Action.ServerEvent(payload) =>
          val event = upickle.default.read[Event](payload)

          event match {

            case Event.TorrentPeersDiscovered(infoHash, count) if model.torrent.exists(_.infoHash == infoHash) =>
              model.torrent.get.stats.modify(_.copy(connected = count))
              model

            case Event.TorrentMetadataReceived(infoHash, name, files) if model.torrent.exists(_.infoHash == infoHash) =>
              model.torrent match {
                case Some(torrent) =>
                  val metadataFiles = files.map { f =>
                    State.Metadata.File(
                      f.path,
                      information.Bytes(f.size)
                    )
                  }
                  val metadata = State.Metadata(name, metadataFiles)
                  val withMetadata = torrent.withMetadata(metadata)
                  model.copy(torrent = Some(withMetadata))

                case _ => model
              }

            case Event.TorrentError(infoHash, message) if model.torrent.exists(_.infoHash == infoHash) =>
              model.copy(
                torrent = model.torrent.map(_.withError(message))
              )

            case Event.Discovered(torrents) =>
              model.copy(
                discovered = model.discovered
                  .fold(State.Discovered(torrents.toList)) { discovered =>
                    discovered.copy(torrents = torrents.toList ++ discovered.torrents)
                  }
                  .some
              )

            case Event.TorrentStats(infoHash, connected, availability)
              if model.torrent.exists(_.infoHash == infoHash) =>
              model.torrent.get.stats.modify( stats =>
                stats.copy(connected = connected, availability = availability)
              )
              model

            case Event.SearchResults(query, entries) =>
              model.search
                .filter(_.query == query)
                .fold(model) { search =>
                  val updated = search.copy(results = Some(entries))
                  model.copy(search = Some(updated))
                }

            case _ =>
              model
          }

        case Action.OpenTorrent(infoHash) =>
          if (model.torrent.exists(_.infoHash == infoHash))
            model
          else {
            send(Command.GetTorrent(infoHash))
            val statsStore = new Store(Torrent.Stats(0, Nil))
            model.copy(torrent = Some(State.Torrent(infoHash, None, statsStore)))
          }
      }
    }
  }

}


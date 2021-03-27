package default

import com.github.lavrov.bittorrent.InfoHash
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.laminar.api.L._


object TorrentPage {

  def apply(infoHash: InfoHash, send: Observer[Command.GetTorrent], events: EventStream[Event.TorrentMetadataReceived]) = {

    val content =
      events
        .filter(_.infoHash == infoHash)
        .startWithNone
        .map {
          case None =>
            List(
              div("Fetching torrent metadata")
            )
          case Some(metadata) =>
            List(
              h1(cls := "title", metadata.name)
            )
        }

    div(
      onMountCallback(_ => send.onNext(Command.GetTorrent(infoHash))),
      children <-- content
    )
  }
}

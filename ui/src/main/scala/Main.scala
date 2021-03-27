package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import org.scalajs.dom
import com.raquo.laminar.api.L._
import io.laminext.websocket._

object Main {

  def main(args: Array[String]): Unit = {

    val ws = WebSocket
      .url("wss://bittorrent-server.herokuapp.com/ws")
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build(managed = true, autoReconnect = true)

    val rootElement =
      div(
        ws.connect,
        child <--
          Routing.router.$currentPage.map {
            case Routing.Page.Root =>
              SearchPage(ws.send, ws.received.collect { case r: Event.SearchResults => r })
            case Routing.Page.Torrent(infoHash) =>
              TorrentPage(infoHash)
          }
      )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

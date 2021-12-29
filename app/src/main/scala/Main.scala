package default

import scala.scalajs.js
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.ownership.ManualOwner
import org.scalajs.dom
import org.scalajs.dom.console
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import io.laminext.websocket.*
import pages.{SearchPage, TorrentPage, HandleUrlPage}
import scala.concurrent.ExecutionContext.Implicits.global
import dom.experimental.serviceworkers._

import com.raquo.laminar.nodes.ReactiveElement
import scala.scalajs.js.annotation.JSImport

object Main {

  @js.native
  @JSImport("@fortawesome/fontawesome-free/js/all", JSImport.Default)
  val fontawesome: js.Object = js.native

  def init(): Unit = {

    val _ = fontawesome

    val ws = WebSocket
      .url("wss://bittorrent-server.herokuapp.com/ws")
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build(managed = true, autoReconnect = true)

    val rootElement =
      App(
        ws.connect,
        child <-- SplitRender[Routing.Page, HtmlElement](Routing.router.$currentPage)
          .collectSignal[Routing.Page.Root] { $page =>
            SearchPage(
              $page.map(_.query),
            )
          }
          .collect[Routing.Page.Torrent] { page =>
            TorrentPage(
              page.infoHash,
              ws.send,
              ws.received.collect {
                case r: Event.TorrentMetadataReceived => r
              }
            )
          }
          .collect[Routing.Page.HandleUrl] { page =>
            HandleUrlPage(page.url)
          }
          .$view
      )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)

    val location = dom.window.location

    dom.window.navigator.asInstanceOf[js.Dynamic].registerProtocolHandler(
      "magnet",
      s"${location.protocol}//${location.host}/handle?url=%s",
      "TorrentDam"
    )
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

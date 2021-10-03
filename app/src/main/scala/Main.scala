package default

import scala.scalajs.js
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.ownership.ManualOwner
import org.scalajs.dom
import org.scalajs.dom.console
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import io.laminext.websocket.*
import pages.{SearchPage, TorrentPage}
import scala.concurrent.ExecutionContext.Implicits.global
import dom.experimental.serviceworkers._

import util.chaining.scalaUtilChainingOps
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

    val searchService = 
      if !js.isUndefined(dom.window.navigator.serviceWorker) then
        console.log("ServiceWorker support: ok")
        Some(
          Signal
            .fromFuture(SearchService.serviceWorkerBased(dom.window.navigator.serviceWorker)
          )
        )
      else
        None

    val indexStatus = searchService match
      case Some(signal) => IndexStatus.Supported(signal.map(_.isDefined))
      case None => IndexStatus.NotSupported

    val rootElement =
      App(
        indexStatus,
        ws.connect,
        child <-- SplitRender[Routing.Page, HtmlElement](Routing.router.$currentPage)
          .collectSignal[Routing.Page.Root] { $page =>
            SearchPage(
              $page.map(_.query),
              searchService.getOrElse(Signal.fromValue(None))
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
          .$view
      )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

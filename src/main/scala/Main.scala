package default

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

object Main {

  def init(): Unit = {

    val ws = WebSocket
      .url("wss://bittorrent-server.herokuapp.com/ws")
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build(managed = true, autoReconnect = true)

    val activeServiceWorker = EventStream
      .fromJsPromise(
        dom.window.navigator.serviceWorker.ready
      )
      .map(_.active)

    val searchServiceVar = Var(Option.empty[SearchService]).tap { it =>
      if dom.window.navigator.serviceWorker != null then
        console.log("ServiceWorker support: ok")
        SearchService
          .serviceWorkerBased(dom.window.navigator.serviceWorker)
          .foreach( service => it.set(Some(service)))
    }

    val rootElement =
      div(
        ws.connect,
        child <-- SplitRender[Routing.Page, HtmlElement](Routing.router.$currentPage)
          .collectSignal[Routing.Page.Root] { $page =>
            SearchPage(
              $page.map(_.query),
              searchServiceVar.signal
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

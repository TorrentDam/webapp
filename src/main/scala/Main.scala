package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.ownership.ManualOwner
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import io.laminext.websocket.*
import pages.{SearchPage, TorrentPage}
import scala.concurrent.ExecutionContext.Implicits.global
import dom.experimental.serviceworkers._

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

    val searchResultsVar = Var(Option.empty[TorrentIndex.Results])

    dom.window.navigator.serviceWorker.onmessage = (event) =>
      io.circe.parser.parse(event.data.toString)
        .flatMap(_.as[TorrentIndex.Results])
        .foreach { results =>
          searchResultsVar.set(Some(results))
        }

    def requestSearch(query: String): Unit =
      if dom.window.navigator.serviceWorker.controller != null
      then
        dom.window.navigator.serviceWorker.controller.postMessage(query)

    val rootElement =
      div(
        ws.connect,
        child <-- SplitRender[Routing.Page, HtmlElement](Routing.router.$currentPage)
          .collectSignal[Routing.Page.Root] { $page =>
            SearchPage(
              $page.map(_.query),
              requestSearch,
              searchResultsVar.signal
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
    registerServiceWorker()
  }

  def registerServiceWorker(): Unit = {

    dom.window.navigator
      .serviceWorker
      .register("/sw.js")
      .toFuture
      .map { registration =>
        dom.console.log("ServiceWorker registered")
      }
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

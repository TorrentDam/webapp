package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.ownership.ManualOwner
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import io.laminext.websocket.*
import pages.{SearchPage, TorrentPage}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.chaining.*
import org.scalajs.dom.experimental.Fetch

object Main {

  def init(): Unit = {

    val ws = WebSocket
      .url("wss://bittorrent-server.herokuapp.com/ws")
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build(managed = true, autoReconnect = true)

    val indexVar = Var[Option[TorrentIndex]](None).tap { it =>
      val url = "https://raw.githubusercontent.com/TorrentDam/torrents/master/index/index.json"
      for
        response <- Fetch.fetch(url).toFuture
        body <- response.text.toFuture
      do
        val entries = TorrentIndex.Entries.fromString(body).toTry.get
        val index = TorrentIndex(entries)
        it.set(Some(index))
    }

    val searchResultsVar = Var(Option.empty[TorrentIndex.Results])

    def requestSearch(query: String): Unit =
      indexVar.now() match
        case Some(index) =>
          val results = index.search(query)
          searchResultsVar.set(Some(results))
        case None =>

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
    import dom.experimental.serviceworkers._

    dom.window.navigator
      .serviceWorker
      .register("/sw.js")
      .toFuture
      .map { _ =>
        dom.console.log("ServiceWorker registered")
      }
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

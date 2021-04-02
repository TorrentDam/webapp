package default

import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import com.raquo.airstream.ownership.ManualOwner
import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.waypoint.SplitRender
import io.laminext.websocket._
import pages.{SearchPage, TorrentPage}

object Main {

  def main(args: Array[String]): Unit = {

    val ws = WebSocket
      .url("wss://bittorrent-server.herokuapp.com/ws")
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build(managed = true, autoReconnect = true)

    val searchCache = Var(Map.empty[String, Event.SearchResults])

    val searchResultsVar = Var(Option.empty[Event.SearchResults])

    ws.received.foreach {
      case results: Event.SearchResults =>
        searchCache.update(_.updated(results.query, results))
        searchResultsVar.set(Some(results))
      case _ =>
    }(new ManualOwner)

    def requestSearch(query: String): Unit = {
      println(s"Requested: $query")
      searchCache.now().get(query) match {
        case Some(results) => searchResultsVar.set(Some(results))
        case None => ws.sendOne(Command.Search(query))
      }
    }

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
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

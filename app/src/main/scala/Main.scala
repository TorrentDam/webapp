package default

import scala.scalajs.js
import com.github.lavrov.bittorrent.app.protocol.{Command, Event}
import org.scalajs.dom
import org.scalajs.dom.console
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.SplitRender
import io.laminext.websocket.*
import pages.{SearchPage, TorrentPage}

import scala.concurrent.ExecutionContext.Implicits.global
import dom.experimental.serviceworkers.*
import com.raquo.laminar.nodes.ReactiveElement
import util.MagnetLink

import scala.scalajs.js.annotation.JSImport

object Main {

  @js.native
  @JSImport("@fortawesome/fontawesome-free/js/all", JSImport.Default)
  val fontawesome: js.Object = js.native

  def init(): Unit = {

    val _ = fontawesome

    dom.window.console.log(Config)

    val ws = WebSocket
      .url(Config.websocketUrl("ws"))
      .receiveText(stringToEvent)
      .sendText(commandToString)
      .build()

    val currentTorrentVar: Var[Option[MagnetLink]] = Var(None)

    def runSubscriptions(using Owner): Unit =
      ws.connected.startWithNone.combineWith(currentTorrentVar).foreach {
        case (Some(_), Some(magnet)) => ws.sendOne(Command.RequestTorrent(magnet.infoHash, magnet.trackers))
        case _ =>
      }

    val rootElement =
      App(
        ws.isConnected,
        ws.connect,
        onMountCallback(ctx => runSubscriptions(using ctx.owner)),
        child <-- SplitRender[Routing.Page, HtmlElement](Routing.router.$currentPage)
          .collectSignal[Routing.Page.Root] { $page =>
            SearchPage(
              $page.map(_.query),
            )
          }
          .collect[Routing.Page.Torrent] { page =>
            MagnetLink.fromString(page.url) match
              case Some(magnet) =>
                TorrentPage(
                  magnet,
                  ws.send,
                  ws.received.collect {
                    case e: Event.TorrentMetadataReceived if e.infoHash == magnet.infoHash => e
                    case e: Event.TorrentStats if e.infoHash == magnet.infoHash => e
                  }
                ).amend(
                  onMountCallback(_ => currentTorrentVar.set(Some(magnet))),
                )
              case _ =>
                div("Invalid url")
          }
          .$view
      )

    val containerNode = dom.document.querySelector("#root")

    render(containerNode, rootElement)

    val location = dom.window.location

    dom.window.navigator.asInstanceOf[js.Dynamic].registerProtocolHandler(
      "magnet",
      s"${location.protocol}//${location.host}/torrent?url=%s",
      "TorrentDam"
    )

    dom.window.navigator.serviceWorker
      .register("/sw.js")
      .toFuture
      .foreach { registration =>
        console.log("ServiceWorker registered")
      }
  }

  def stringToEvent(value: String): Either[Throwable, Event] = Right(upickle.default.read[Event](value))

  def commandToString(command: Command): String = upickle.default.write(command)
}

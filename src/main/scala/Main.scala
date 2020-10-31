import org.scalajs.dom
import slinky.web.ReactDOM
import cats.implicits._
import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.IORuntime
import com.github.lavrov.bittorrent.app.protocol.Command
import component.App
import logic.{Action, Dispatcher, Handler, Store, State}
import component.{Connect, Router}

import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  @inline
  implicit def runtime: IORuntime = IORuntime.global

  @JSExportTopLevel("main")
  def main(): Unit = {
    initialize
      .map {
        case (model, dispatcher) =>
          ReactDOM.render(
            Router(Connect(model)(model => App(model, dispatcher))),
            dom.document.getElementById("root")
          )
      }
      .unsafeRunAndForget()
  }

  def initialize = {
    for {
      out <- Queue.unbounded[IO, String]
      model <- IO { new Store(State.initial) }
      dispatcher <- IO {
        def send(command: Command): Unit = {
          val str = upickle.default.write(command)
          out.offer(str).unsafeRunAndForget()
        }
        lazy val dispatcher: Dispatcher = {
          val handler = Handler(send)
          Dispatcher(handler, model)
        }
        dispatcher
      }
      socket <- ReconnectingSocket.create(
        environment.wsUrl("/ws"),
        msg =>
          IO { org.scalajs.dom.console.info(s"WS << $msg") } >>
            IO { dispatcher(Action.ServerEvent(msg)) },
        connected => IO { dispatcher(Action.UpdateConnectionStatus(connected)) }
      )
      _ <-
        out.take
          .flatMap { msg =>
            IO { org.scalajs.dom.console.info(s"WS >> $msg") } >>
              socket.send(msg)
          }
          .foreverM
          .start
    } yield {
      (model, dispatcher)
    }
  }

}

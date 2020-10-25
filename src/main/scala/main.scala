import org.scalajs.dom
import slinky.web.ReactDOM
import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp, SyncIO}
import cats.effect.std.Queue
import cats.effect.unsafe.IORuntime
import com.github.lavrov.bittorrent.app.protocol.Command
import component.{App, Router}
import logic.{Action, Dispatcher, Handler, Store, WindowTitle}
import logic.model.Root
import component.Connect
import slinky.core.FunctionalComponent
import slinky.core.facade.Hooks

import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  @inline
  implicit def runtime: IORuntime = IORuntime.global

  @JSExportTopLevel("main")
  def main(): Unit = {
    initialize.map {
      case (model, router, dispatcher) =>
        ReactDOM.render(
          Connect(model)(model => App(router, model, dispatcher)),
          dom.document.getElementById("root")
        )
    }
      .unsafeRunAndForget()
//    ReactDOM.render(
//      RootComponent(),
//      dom.document.getElementById("root")
//    )
  }

  val RootComponent = FunctionalComponent[Unit] { _ =>
    val (model, router, dispatcher) = Hooks.useMemo(() => initialize.unsafeToFuture().value.get.get, List.empty)
    Connect(model)(model => App(router, model, dispatcher))
  }

  def initialize = {
    for {
      out <- Queue.unbounded[IO, String]
      model <- IO { new Store(Root.initial) }
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
      router <- IO { Router() }
      _ <- IO {
        dispatcher(Action.Navigate(router.current))
        router.onNavigate(route => dispatcher(Action.Navigate(route)))
      }
      _ <- IO {
        model.subscribe { model =>
          dom.document.title = WindowTitle.fromModel(model)
        }
      }
    } yield {
      (model, router, dispatcher)
    }
  }

}

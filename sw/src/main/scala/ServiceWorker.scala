package default

import org.scalajs.dom.console
import org.scalajs.dom.ServiceWorkerGlobalScope._


object ServiceWorker {

  def init(): Unit =
      console.log("ServiceWorker: init")

    self.oninstall = (event) =>
      console.log("ServiceWorker: install")

    self.onactivate = (event) =>
      console.log("ServiceWorker: activate")

}

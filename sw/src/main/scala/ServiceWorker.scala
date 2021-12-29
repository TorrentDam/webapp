package default

import org.scalajs.dom.{console, fetch}
import org.scalajs.dom.ServiceWorkerGlobalScope.*


object ServiceWorker {

  def init(): Unit =
      console.log("ServiceWorker: init")

    self.oninstall = (event) =>
      console.log("ServiceWorker: install")

    self.onactivate = (event) =>
      console.log("ServiceWorker: activate")

    self.onfetch = (event) =>
      event.respondWith(fetch(event.request))
}

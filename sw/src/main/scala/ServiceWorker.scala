package default

import org.scalajs.dom.console
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._
import org.scalajs.dom.experimental.Fetch
import org.scalajs.dom.experimental.Response
import org.scalajs.dom.raw.MessagePort

import scala.scalajs.js.Array
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.chaining.scalaUtilChainingOps

import scala.scalajs.js.JSConverters.*
import io.circe.syntax.EncoderOps


object ServiceWorker {

  def init(): Unit =

    console.log("ServiceWorker.init")

    val indexPromise = scala.concurrent.Promise[TorrentIndex].tap { promise =>
      val url = "https://raw.githubusercontent.com/TorrentDam/torrents/master/index/index.json"
      console.log(s"Fetch $url")
      for
        response <- Fetch.fetch(url).toFuture
        body <- response.text.toFuture
      yield
        console.log("Parse index")
        val entries = TorrentIndex.Entries.fromString(body).toTry.get
        promise.success(TorrentIndex(entries))
        console.log("Parse finished")
    }

    self.oninstall = (event) =>
      console.log("Install event")
      event.waitUntil(self.skipWaiting)

    self.onactivate = (event) =>
      console.log("Activate event")
      event.waitUntil(self.clients.claim())

    self.onmessage = (event) =>
      val query = event.data.asInstanceOf[String]
      console.log(s"Search: $query")
      for
        index <- indexPromise.future
      do
        val results = index.search(query)
        event.source.postMessage(results.asJson.noSpaces, null)
}

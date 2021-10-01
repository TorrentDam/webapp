package default

import org.scalajs.dom.console
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._
import org.scalajs.dom.experimental.Fetch
import org.scalajs.dom.experimental.Response
import org.scalajs.dom.raw.MessagePort

import scala.scalajs.js
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

import scala.scalajs.js.Dynamic
import scala.scalajs.js.Dynamic.{literal as obj}
import scala.scalajs.js.JSConverters.*
import io.circe.syntax.EncoderOps
import org.scalajs.dom.raw.IDBFactory
import org.scalajs.dom.raw.IDBDatabase
import scala.concurrent.Promise
import org.scalajs.dom.raw.IDBCursor


object ServiceWorker {

  def init(): Unit =

      console.log("ServiceWorker: init")

    val idb = scala.scalajs.js.Dynamic.global.indexedDB.asInstanceOf[IDBFactory]

    val database = Promise[IDBDatabase]

    def torrentsStore =
      for
        database <- database.future
      yield
        val tx = database.transaction("torrents", "readonly")
        tx.objectStore("torrents")

    idb.open("index").tap { request =>
      request.onupgradeneeded = (event) =>
        console.log("Upgrading database")
        val db = (event.target.asInstanceOf[Dynamic]).result.asInstanceOf[IDBDatabase]
        val store = db.createObjectStore("torrents", obj(keyPath = "infoHash"))
        store.createIndex("name", "name", obj(unique = false))
      request.onsuccess = (event) =>
        console.log("Open database: success")
        val db = (event.target.asInstanceOf[Dynamic]).result.asInstanceOf[IDBDatabase]
        database.success(db)
    }

    self.oninstall = (event) =>
      console.log("ServiceWorker: install")
      val indexJson =
        val url = "https://raw.githubusercontent.com/TorrentDam/torrents/master/index/index.json"
        console.log(s"Fetch $url")
        for
          response <- Fetch.fetch(url).toFuture
          body <- response.json.toFuture
        yield
          console.log("Fetch complete")
          body.asInstanceOf[js.Array[js.Object]]
      val storeComplete = Promise[Any]
      for
        indexJson <- indexJson
        database <- database.future
      yield
        val tx = database.transaction("torrents", "readwrite")
        val store = tx.objectStore("torrents")
        store.clear()
        console.log("Store torrents")
        indexJson.foreach { value =>
          store.add(value)
        }
        tx.oncomplete = (event) =>
          console.log("Store complete")
          storeComplete.success(true)
      event.waitUntil(storeComplete.future.toJSPromise)

    self.onactivate = (event) =>
      console.log("ServiceWorker: activate")
      event.waitUntil(self.clients.claim())

    self.onmessage = (event) =>
      val source = event.source
      val query = event.data.asInstanceOf[String]
      console.log("ServiceWorker: message")
      console.log(s"Search: $query")
      for
        store <- torrentsStore
      do
        val c = store.index("name").openKeyCursor()
        var list = List.empty[String]
        val complete = Promise[Unit]
        c.onsuccess = (event) =>
          val cursor = (event.target.asInstanceOf[Dynamic]).result.asInstanceOf[IDBCursor]
          if(cursor != null && list.size < 10) then
            if cursor.key.asInstanceOf[String].toLowerCase.contains(query) then
              console.log(s"Cursor: ${cursor.primaryKey}")
              val pk = cursor.primaryKey.asInstanceOf[String]
              list = pk :: list
            cursor.continue()
          else
            complete.success(())
        def get(pk: String) =
          console.log(s"getting: $pk")
          val promise = Promise[TorrentIndex.Entry]
          store.get(pk).onsuccess = (event) =>
            val value = (event.target.asInstanceOf[Dynamic]).result.asInstanceOf[js.Object]
            val str = js.JSON.stringify(value)
            for
              json <- io.circe.parser.parse(str)
              entry <- json.as[TorrentIndex.Entry]
            do
              promise.success(entry)
          promise.future
        
        for
          _ <- complete.future
          entries <- Future.traverse(list.reverse)(pk => get(pk))
        do
          val results = TorrentIndex.Results(query, entries)
          source.postMessage(results.asJson.noSpaces, null)
}

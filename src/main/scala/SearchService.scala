package default

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerContainer
import org.scalajs.dom.console
import com.github.lavrov.bittorrent.InfoHash
import io.circe.syntax.EncoderOps

trait SearchService {
  import SearchService.Results

  def search(query: String): Future[Results]
}

object SearchService {

  case class Results(query: String, entries: List[Entry])
  case class Entry(name: String, infoHash: InfoHash, size: Long, ext: List[String])

  def serviceWorkerBased(container: ServiceWorkerContainer): Future[SearchService] =
    case class Callbacks(
      var searchResponse: Promise[Results] | Null = null
    )
    container
      .register("/sw.js")
      .toFuture
      .flatMap { registration =>
        console.log("ServiceWorker registered")
        container.ready.toFuture.map(r => r.active)
      }
      .map { serviceWorker =>
        val callbacks = Callbacks()
        container.onmessage = (event) =>
          io.circe.parser
            .parse(event.data.toString)
            .flatMap(_.as[WindowMessage])
            .foreach {
              case WindowMessage.SearchResults(query, entries) =>
                if callbacks.searchResponse != null then
                  val results = Results(
                    query,
                    entries.map(
                      e => Entry(e.name, e.infoHash, e.size, e.ext)
                    )
                  )
                  callbacks.searchResponse.success(results)
            }
        new SearchService {

          def search(query: String) =
            callbacks.searchResponse = Promise[Results]
            val message = ServiceWorkerMessage.Query(query)
            serviceWorker.postMessage(message.asJson.noSpaces)
            callbacks.searchResponse.future
        }
      }
}
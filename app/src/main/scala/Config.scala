package default

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("/config.js", JSImport.Default)
object JsConfig extends js.Object {

  def server: String = js.native

  def secure: Boolean = js.native
}

object Config {

  private val httpBase = (if JsConfig.secure then "https" else "http") + "://" + JsConfig.server
  private val websocketBase = (if JsConfig.secure then "wss" else "ws") + "://" + JsConfig.server

  def httpUrl(path: String): String = s"$httpBase/$path"
  def websocketUrl(path: String): String = s"$websocketBase/$path"
}

package util

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("__config")
object JsConfig extends js.Object {
  def server: String = js.native
  def secure: Boolean = js.native
  def basePath: String = js.native
}

object Config {
  private val httpBase = (if JsConfig.secure then "https" else "http") + "://" + JsConfig.server
  private val websocketBase = (if JsConfig.secure then "wss" else "ws") + "://" + JsConfig.server

  val basePath: String =
    if JsConfig.basePath == "/" then "" else JsConfig.basePath

  def httpUrl(path: String): String = s"$httpBase/$path"
  def websocketUrl(path: String): String = s"$websocketBase/$path"

  def relativeUrl(path: String): String = basePath + path
}

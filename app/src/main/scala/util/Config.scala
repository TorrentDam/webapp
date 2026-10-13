package util

import scala.scalajs.js

object Config {
  private val rawConfig = js.Dynamic.global.__config.asInstanceOf[js.Dictionary[js.Any]]
  private val server = rawConfig("server").asInstanceOf[String]
  private val secure = rawConfig("secure").asInstanceOf[Boolean]

  private val httpBase = (if secure then "https" else "http") + "://" + server
  private val websocketBase = (if secure then "wss" else "ws") + "://" + server

  private val configScript = js.Dynamic.global.document.querySelector("""script[data-role="config"]""")

  private val configPath: Option[String] = Option(configScript).flatMap { script =>
    val src = script.asInstanceOf[js.Dynamic].src.asInstanceOf[String]
    val schemeEnd = src.indexOf("://")
    if schemeEnd < 0 then None
    else
      val pathStart = src.indexOf('/', schemeEnd + 3)
      if pathStart < 0 then None else Some(src.substring(pathStart))
  }

  val basePath: String = configPath.fold("") { path =>
    val withoutTrailingSlash = if path.endsWith("/") then path.substring(0, path.length - 1) else path
    val directory = withoutTrailingSlash.substring(0, withoutTrailingSlash.lastIndexOf('/') + 1)
    if directory == "/" then "" else directory
  }

  def httpUrl(path: String): String = s"$httpBase/$path"
  def websocketUrl(path: String): String = s"$websocketBase/$path"

  def relativeUrl(path: String): String = basePath + path
}

package default

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("/config.js", JSImport.Default)
object Config extends js.Object {

  def server: String = js.native
}

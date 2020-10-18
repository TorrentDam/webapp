
package logic

import logic.model.Root
import monix.reactive.subjects.Var

trait Dispatcher {
  def apply(action: Action): Unit
}

object Dispatcher {

  def apply(handler: Handler, state: Var[Root]): Dispatcher =
    action => state := handler(state(), action)
}

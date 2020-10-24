
package logic

import logic.model.Root

trait Dispatcher {
  def apply(action: Action): Unit
}

object Dispatcher {

  def apply(handler: Handler, state: Store[Root]): Dispatcher =
    action => state.update(handler(state.current, action))
}

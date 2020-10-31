
package logic

trait Dispatcher {
  def apply(action: Action): Unit
}

object Dispatcher {

  def apply(handler: Handler, state: Store[State]): Dispatcher =
    action => state.update(handler(state.current, action))
}

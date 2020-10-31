package logic

class Store[A](var current: A) {

  private var callbacks: List[Store.Callback[A]] = List.empty

  def update(`new`: A): Unit = {
    current = `new`
    callbacks.foreach(_(current))
  }

  def modify(f: A => A): Unit = {
    update(f(current))
  }

  def subscribe(f: Store.Callback[A]): Store.Unsubscribe = {
    callbacks = f :: callbacks

    { () =>
      callbacks = callbacks.filterNot(_ eq f)
    }
  }
}

object Store {

  type Callback[A] = A => Unit
  type Unsubscribe = () => Unit
}
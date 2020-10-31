package component

import logic.Store
import slinky.core.FunctionalComponent
import slinky.core.facade.{Hooks, ReactElement}

object Connect {

  def apply[Model, R](store: Store[Model])(
    component: Model => ReactElement
  ): ReactElement = {

    val wrapper = FunctionalComponent[Unit] { _ =>
      val (state, setState) = Hooks.useState(store.current)
      def subscribe(): () => Unit = {
        val unsubscribe = store.subscribe(setState)
        () => unsubscribe()
      }
      Hooks.useEffect(subscribe, List(true))
      component(state)
    }

    wrapper()
  }
}

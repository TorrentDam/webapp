package component

import logic.Store
import slinky.core.FunctionalComponent
import slinky.core.facade.{Hooks, ReactElement}

object Connect {

  def apply[Model, R](observable: Store[Model])(
    component: Model => ReactElement
  ): ReactElement = {

    val wrapper = FunctionalComponent[Unit] { _ =>
      val (state, setState) = Hooks.useState(observable.current)
      def subscribe(): () => Unit = {
        val unsubscribe = observable.subscribe(setState)
        () => unsubscribe()
      }
      Hooks.useEffect(subscribe, List(true))
      component(state)
    }

    wrapper()
  }
}

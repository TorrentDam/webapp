package component.material_ui

import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.annotation.JSImport

import org.scalajs.dom

package core {
  import slinky.core.facade.ReactElement
  import scala.scalajs.js.|

  @react
  object AppBar extends ExternalComponentWithAttributes[*.tag.type] {
    case class Props(position: String, color: UndefOr[String] = js.undefined, className: UndefOr[String] = js.undefined)

    val component = jsImport

    @JSImport("@material-ui/core/AppBar", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Button extends ExternalComponent {
    case class Props(
      variant: String = "text",
      color: String = "primary",
      disabled: UndefOr[Boolean] = js.undefined,
      `type`: UndefOr[String] = js.undefined,
      startIcon: UndefOr[ReactElement] = js.undefined,
      href: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Button", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Breadcrumbs extends ExternalComponent {
    case class Props(className: UndefOr[String] = js.undefined)

    val component = jsImport

    @JSImport("@material-ui/core/Breadcrumbs", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Toolbar extends ExternalComponent {
    case class Props(disableGutters: UndefOr[Boolean] = js.undefined)

    val component = jsImport

    @js.native @JSImport("@material-ui/core/Toolbar", JSImport.Default)
    private def jsImport: js.Object = js.native
  }

  @react
  object IconButton extends ExternalComponent {
    case class Props(
      edge: UndefOr[String] = js.undefined,
      color: String = "inherit",
      `aria-label`: UndefOr[String] = js.undefined,
      href: UndefOr[String] = js.undefined,
      `type`: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/IconButton", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Box extends ExternalComponent {
    case class Props(
      mb: UndefOr[Double] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Box", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Typography extends ExternalComponent {
    case class Props(
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined,
      color: UndefOr[String] = js.undefined,
      fontWeight: UndefOr[String] = js.undefined,
      noWrap: UndefOr[Boolean] = js.undefined,
      display: UndefOr[String] = js.undefined,
      align: UndefOr[String] = js.undefined,
      className: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Typography", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Container extends ExternalComponent {
    case class Props(
      maxWidth: UndefOr[String] = js.undefined,
      className: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Container", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object TextField extends ExternalComponent {
    case class Props(
      id: String,
      name: String,
      label: String
    )

    val component = jsImport

    @JSImport("@material-ui/core/TextField", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Paper extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      elevation: UndefOr[Int] = js.undefined,
      onSubmit: UndefOr[dom.Event => Unit] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Paper", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Card extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Card", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Grid extends ExternalComponent {
    case class Props(
      container: UndefOr[Boolean] = js.undefined,
      item: UndefOr[Boolean] = js.undefined,
      xs: UndefOr[Boolean | Int] = js.undefined,
      direction: UndefOr[String] = js.undefined,
      spacing: UndefOr[Int] = js.undefined,
      zeroMinWidth: UndefOr[Boolean] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Grid", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object InputBase extends ExternalComponent {
    case class Props(
      placeholder: String,
      value: String,
      onChange: SyntheticEvent[dom.html.Input, dom.Event] => Unit,
      className: UndefOr[String] = js.undefined,
      disabled: UndefOr[Boolean] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/InputBase", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Link extends ExternalComponent {
    case class Props(
      href: UndefOr[String] = js.undefined,
      onClick: UndefOr[js.Function0[Unit]] = js.undefined,
      color: UndefOr[String] = js.undefined,
      className: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Link", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object LinearProgress extends ExternalComponent {
    case class Props(
      color: String = "primary",
      value: UndefOr[Int] = js.undefined,
      variant: String = "indeterminate"
    )

    val component = jsImport

    @JSImport("@material-ui/core/LinearProgress", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object List extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      subheader: UndefOr[ReactElement] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/List", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  object ListSubheader extends ExternalComponentNoProps {

    val component = jsImport

    @JSImport("@material-ui/core/ListSubheader", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object ListItem extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      button: UndefOr[Boolean] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/ListItem", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object ListItemText extends ExternalComponent {
    case class Props(
      primary: UndefOr[String | ReactElement],
      secondary: UndefOr[String | ReactElement] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/ListItemText", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  object ListItemSecondaryAction extends ExternalComponentNoProps {

    val component = jsImport

    @JSImport("@material-ui/core/ListItemSecondaryAction", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Divider extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Divider", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  object CssBaseline extends ExternalComponentNoProps {

    val component = jsImport

    @JSImport("@material-ui/core/CssBaseline", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Table extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/Table", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object TableBody extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/TableBody", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object TableRow extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined,
      hover: UndefOr[Boolean] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/TableRow", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object TableCell extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      variant: UndefOr[String] = js.undefined,
      component: UndefOr[String] = js.undefined
    )

    val component = jsImport

    @JSImport("@material-ui/core/TableCell", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object Fade extends ExternalComponent {
    case class Props(in: Boolean)

    val component = jsImport

    @JSImport("@material-ui/core/Fade", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  @react
  object SvgIcon extends ExternalComponent {
    case class Props(
      className: UndefOr[String] = js.undefined,
      component: UndefOr[js.Any] = js.undefined,
      viewBox: UndefOr[String] = js.undefined,
      color: UndefOr[String] = js.undefined
   )

    val component = jsImport

    @JSImport("@material-ui/core/SvgIcon", JSImport.Default) @js.native
    private def jsImport: js.Object = js.native
  }

  object CardActionArea extends ExternalComponentNoProps {
    val component = typings.materialUiCore.cardActionAreaMod.default
  }

}

package object styles {

  @JSImport("@material-ui/core/styles", "makeStyles") @js.native
  def makeStyles(f: js.Function1[js.Dynamic, js.Dynamic]): js.Dynamic = js.native

}

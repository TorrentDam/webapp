package component

import java.net.URLDecoder

import com.github.lavrov.bittorrent.InfoHash
import org.scalajs.dom.window
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, React, ReactElement}
import slinky.web.html.span
import trail.{Path, PathParser, Route}


@react
object Router {

  type Props = ReactElement

  val context = React.createContext(Path("/"))

  val component = FunctionalComponent[Props] { child =>
    val (current, update) = Hooks.useState(parsePath)
    Hooks.useEffect(() => subscribe(update), List.empty)
    context.Provider(current)(child)
  }

  private def parsePath: Path = {
    val str = decode(window.location.hash.drop(1))
    PathParser.parse(if (str.nonEmpty) str else "/")
  }

  private def subscribe(update: Path => Unit): Unit = {
    window.onhashchange = { _ =>
      update(parsePath)
    }
  }

  private def decode(value: String) = URLDecoder.decode(value, "UTF-8")
}

object MatchRoute {

  type Props[A] = A => ReactElement

  def apply[A](route: Route[A]) = FunctionalComponent[Props[A]]{ child =>
    val path = Hooks.useContext(Router.context)
    route.parseArgsStrict(path) match {
      case Some(a) => child(a)
      case None    => span()
    }
  }
}

object SwitchRoute {

  type Case = (Route[A], A => ReactElement) forSome { type A }

  case class Builder(cases: Seq[Case]) {

    def route[A](route: Route[A])(child: A => ReactElement): Builder = Builder(cases :+ (route, child))

    def default(child: => ReactElement): ReactElement = apply(child)(cases)
  }

  def builder: Builder = Builder(Nil)

  def apply(default: => ReactElement) = FunctionalComponent[Seq[Case]]{ cases =>
    val path = Hooks.useContext(Router.context)
    cases.view
      .map {
        case (route, render) =>
          route.parseArgsStrict(path).map(render)
      }
      .collectFirst {
        case Some(element) => element
      }
      .getOrElse(default)
  }
}

object Navigate {

  def apply(path: String): Unit = {

    window.location.hash = path
  }
}

object Routes {

  import trail._
  import Codecs._

  val root: Route[Unit] = Root

  val search: Route[String] = root / "search" & Param[String]("q")

  val torrent: Route[InfoHash] = root / "torrent" / Arg[InfoHash]

  val torrentFile: Route[(InfoHash, Int)] = torrent / "file" / Arg[Int]
}

object Codecs {
  import trail.Codec

  implicit val InfoHashCodec: Codec[InfoHash] =
    new Codec[InfoHash] {
      def encode(value: InfoHash): Option[String] = Some(value.toString())
      def decode(value: Option[String]): Option[InfoHash] = value.flatMap(InfoHash.fromString.lift)
    }
}

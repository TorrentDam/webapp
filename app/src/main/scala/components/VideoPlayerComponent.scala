package components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.CustomHtmlTag
import org.scalajs.dom

def VideoPlayerComponent(name: String, src: String): ReactiveHtmlElement[dom.html.Div] =
  val mediaPlayerTag = new CustomHtmlTag[dom.html.Element]("media-player")
  val mediaProviderTag = new CustomHtmlTag[dom.html.Element]("media-provider")
  val mediaVideoLayoutTag = new CustomHtmlTag[dom.html.Element]("media-video-layout")

  // The data URL has no file extension, so vidstack cannot infer the media type
  // and falls back to a HEAD probe the torrent server does not answer. A fake
  // extension in an ignored query parameter makes inference succeed; the browser
  // sniffs the actual container regardless of the declared type.
  val srcWithTypeHint =
    val separator = if src.contains("?") then "&" else "?"
    s"$src${separator}v=.mp4"

  div(
    cls := "video-player",
    mediaPlayerTag(
      titleAttr := name,
      srcAttr := srcWithTypeHint,
      playsInlineAttr := "true",
      loadAttr := "visible",
      viewTypeAttr := "video",
      mediaProviderTag(),
      mediaVideoLayoutTag(
        seekStepAttr := "10"
      )
    )
  )
end VideoPlayerComponent

private val titleAttr = htmlAttr("title", StringAsIsCodec)
private val srcAttr = htmlAttr("src", StringAsIsCodec)
private val playsInlineAttr = htmlAttr("playsinline", StringAsIsCodec)
private val loadAttr = htmlAttr("load", StringAsIsCodec)
private val viewTypeAttr = htmlAttr("view-type", StringAsIsCodec)
private val seekStepAttr = htmlAttr("seek-step", StringAsIsCodec)

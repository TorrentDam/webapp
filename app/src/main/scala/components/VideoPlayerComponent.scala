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

  div(
    cls := "video-player",
    mediaPlayerTag(
      titleAttr := name,
      srcAttr := src,
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

package sbolv

import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom
import scalatags.generic.{Attr, AttrValue}
import scalatags.JsDom.implicits._
import scalatags.JsDom.svgTags._
import scalatags.JsDom.svgAttrs.{SeqNode => _, _}
import Enhancements.DynamicApply

/**
 *
 *
 * @author Matthew Pocock
 */
trait Animations {
  self : Modifiable =>

  implicit class RangeEnhancer(val _rng: Range) {
    def dur(duration: String): SVGAnimationTemplate = SVGAnimationTemplate(
      from = _rng.start.toString,
      to = _rng.end.toString,
      dur = duration)
  }

  // attributeName and attributeTime are worked out once this is bound to a named attribute
  case class SVGAnimationTemplate(dur: String,
                                  from: String,
                                  to: String,
                                  begin: String = "indefinite",
                                  fill: String = "remove",
                                  modifiers: List[JsDom.Modifier] = Nil)
  {
    def freeze = copy(fill = "freeze")

    def withFill(fill: String) = copy(fill = fill)

    def modifyWith(mods: JsDom.Modifier*) = copy(modifiers = modifiers ++ mods)
  }

  implicit def animatedAttrValue: AttrValue[dom.Element, SVGAnimationTemplate] = new AttrValue[dom.Element, SVGAnimationTemplate] {
    override def apply(t: dom.Element, a: Attr, v: SVGAnimationTemplate) = {
      t.modifyWith(
        animate(
          attributeType := "CSS",
          attributeName := a.name,
          from := v.from,
          to := v.to,
          dur := v.dur,
          begin := v.begin,
          fill := v.fill,
          v.modifiers,
          modifyWith { el =>
            dom.window.setTimeout({() => js.Dynamic(el).beginElement()}, 1)
          }
        )
      ).render
    }
  }
}

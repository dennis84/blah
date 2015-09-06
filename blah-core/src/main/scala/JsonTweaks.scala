package blah.core

import spray.json._

trait JsonTweaks {
  implicit class Merge(a: JsObject) {
    def merge(b: JsObject): JsObject = {
      val JsObject(a1) = a
      val JsObject(b1) = b
      val result = a1 ++ b1.map {
        case (otherKey, otherValue) =>
          val maybeExistingValue = a1.get(otherKey)
          val newValue = (maybeExistingValue, otherValue) match {
            case (Some(e: JsObject), o: JsObject) => e.merge(o)
            case _ => otherValue
          }
          otherKey -> newValue
      }
      JsObject(result)
    }
  }
}

object JsonTweaks extends JsonTweaks

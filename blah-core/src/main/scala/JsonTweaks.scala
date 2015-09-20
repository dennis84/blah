package blah.core

import spray.json._

trait JsonTweaks {
  implicit class Merge(a: JsObject) {
    def merge(b: JsObject): JsObject = {
      val result = a.fields ++ b.fields.map {
        case (otherKey, otherValue) =>
          val maybeExistingValue = a.fields.get(otherKey)
          val newValue = (maybeExistingValue, otherValue) match {
            case (Some(e: JsObject), o: JsObject) => e merge o
            case (Some(e: JsArray), o: JsArray) => JsArray(e.elements ++ o.elements)
            case _ => otherValue
          }
          otherKey -> newValue
      }
      JsObject(result)
    }
  }
}

object JsonTweaks extends JsonTweaks

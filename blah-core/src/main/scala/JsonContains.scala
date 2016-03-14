package blah.core

import spray.json._

trait JsonContains {
  implicit class JsonContainsFn(a: JsObject) {
    def contains(b: JsObject): Boolean = b.fields forall {
      case (k, v) => a.fields get k map(_ == v) getOrElse(false)
    }
  }
}

object JsonContains extends JsonContains

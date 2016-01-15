package blah.core

import spray.json._

trait JsonDsl extends JsonMerge {
  implicit def int2Js(x: Int) = JsNumber(x)
  implicit def string2Js(x: String) = JsString(x)
  implicit def boolean2Js(x: Boolean) = JsBoolean(x)
  implicit def traversable2Js[A](xs: Traversable[A])(implicit ev: A => JsValue) =
    JsArray(xs.toVector map (x => x : JsValue))
  implicit def pair2Js[A](x: (String, A))(implicit ev: A => JsValue) =
    JsObject(fields = Map(x._1 -> x._2))
  implicit def pair2Assoc[A](x: (String, A))(implicit ev: A => JsValue) =
    new JsonAssoc(x)
  implicit def jsObject2Assoc(x: JsObject) = new JsonListAssoc(x)

  class JsonAssoc[A](left: (String, A))(implicit ev: A => JsValue) {
    def ~[B](right: (String, B))(implicit ev1: B => JsValue) =
      JsObject(left._1 -> left._2, right._1 -> right._2)

    def ~(right: JsObject) = {
      val l: JsValue = left._2
      JsObject(Map(left._1 -> l) ++ right.fields)
    }
  }

  class JsonListAssoc(left: JsObject) {
    def ~(right: (String, JsValue)) =
      JsObject(left.fields ++ Map(right._1 -> right._2))

    def ~(right: JsObject) =
      JsObject(left.fields ++ right.fields)
  }
}

object JsonDsl extends JsonDsl

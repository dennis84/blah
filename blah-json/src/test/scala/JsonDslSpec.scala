package blah.json

import org.scalatest._
import spray.json._
import JsonDsl._

class JsonDslSpec extends FlatSpec with Matchers {
  "JsonDsl in scope" should "works" in {
    (1 : JsNumber) should be (JsNumber(1))
    (true : JsBoolean) should be (JsBoolean(true))
    ("a" : JsString) should be (JsString("a"))
    (Seq(1, 2) : JsArray) should be (JsArray(1, 2))
    (("a" -> "b") : JsObject) should be (JsObject("a" -> JsString("b")))
    val a: JsObject = ("a" -> "b") ~ ("c" -> "d")
    a should be (JsObject("a" -> JsString("b"), "c" -> JsString("d")))
  }
}

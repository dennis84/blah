package blah.core

import org.scalatest._
import spray.json._
import JsonTweaks._

class JsonMergeSpec extends FlatSpec with Matchers {

  "JSON Merge" should "merge two js objects" in {
    JsObject("a" -> JsObject("x" -> JsNumber(1))) merge
    JsObject("a" -> JsObject("x" -> JsNumber(2))) should be (
    JsObject("a" -> JsObject("x" -> JsNumber(2))))

    JsObject("a" -> JsObject("x" -> JsNumber(1), "y" -> JsNumber(1))) merge
    JsObject("a" -> JsObject("x" -> JsNumber(2))) should be (
    JsObject("a" -> JsObject("x" -> JsNumber(2), "y" -> JsNumber(1))))

    JsObject("a" -> JsObject(
      "x" -> JsNumber(1),
      "y" -> JsObject(
        "z" -> JsNumber(3)))) merge
    JsObject("a" -> JsObject(
      "x" -> JsNumber(2),
      "y" -> JsObject(
        "v" -> JsNumber(4)))) should be (
    JsObject("a" -> JsObject(
      "x" -> JsNumber(2),
      "y" -> JsObject(
        "z" -> JsNumber(3),
        "v" -> JsNumber(4)))))
  }
}

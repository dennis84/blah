package blah.core

import org.scalatest._
import spray.json._
import JsonDsl._

class JsonContainsSpec extends FlatSpec with Matchers {

  "JsonContains in scope" should "contains" in {
    val x: JsObject = ("a" -> "b") ~ ("c" -> "d")
    val y: JsObject = ("a" -> "b")
    (x contains y) should be (true)
  }

  it should "contains nested" in {
    val x: JsObject = ("a" -> ("foo" -> "bar")) ~ ("c" -> "d")
    val y: JsObject = ("a" -> ("foo" -> "bar"))
    (x contains y) should be (true)
  }
}

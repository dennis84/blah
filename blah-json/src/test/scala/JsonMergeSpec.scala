package blah.json

import org.scalatest._
import spray.json._
import JsonDsl._

class JsonMergeSpec extends FlatSpec with Matchers {

  "JsonMerge in scope" should "merge two objects" in {
    val x: JsObject = ("a" -> ("x" -> 1))
    val y: JsObject = ("a" -> ("x" -> 2))
    val z: JsObject = ("a" -> ("x" -> 2))
    (x merge y) should be (z)
  }

  it should "merge two objects with missing key" in {
    val x: JsObject = ("a" -> ("x" -> 1) ~ ("y" -> 1))
    val y: JsObject = ("a" -> ("x" -> 2))
    val z: JsObject = ("a" -> ("x" -> 2) ~ ("y" -> 1))
    (x merge y) should be (z)
  }

  it should "merge two objects with different keys" in {
    val x: JsObject = ("a" -> ("x" -> 1) ~ ("y" -> ("z" -> 3)))
    val y: JsObject = ("a" -> ("x" -> 2) ~ ("y" -> ("v" -> 4)))
    val z: JsObject = ("a" -> ("x" -> 2) ~ ("y" -> ("z" -> 3) ~ ("v" -> 4)))
    (x merge y) should be (z)
  }

  it should "merge objects in a list" in {
    val x: JsObject = ("a" -> List("x" -> 1))
    val y: JsObject = ("a" -> List("x" -> 2))
    val z: JsObject = ("a" -> List(("x" -> 1), ("x" -> 2)))
    (x merge y) should be (z)
  }
}

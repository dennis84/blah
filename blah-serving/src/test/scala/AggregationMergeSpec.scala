package blah.serving

import org.scalatest._
import spray.json._
import JsonDsl._
import AggregationMerge._

class AggregationMergeSpec extends FlatSpec with Matchers {

  "AggregationMerge in scope" should "merge two aggs" in {
    val x: JsObject = ("aggs" -> ("country" -> ("terms" -> "...")))
    val y: JsObject = ("aggs" -> ("city" -> ("terms" -> "...")))
    val z: JsObject = ("aggs" ->
                        ("country" ->
                          ("terms" -> "...") ~
                          ("aggs" ->
                            ("city" -> ("terms" -> "...")))))
    (x mergeAggregation y) should be (z)
  }

  it should "merge a list of aggs" in {
    val aggs: List[JsObject] = List(
      ("aggs" -> ("country" -> ("terms" -> "..."))),
      ("aggs" -> ("city" -> ("terms" -> "..."))),
      ("aggs" -> ("street" -> ("terms" -> "..."))))
    val result: JsObject =
      ("aggs" ->
        ("country" ->
          ("terms" -> "...") ~
          ("aggs" ->
            ("city" ->
              ("terms" -> "...") ~
              ("aggs" ->
                ("street" ->
                  ("terms" -> "...")))))))
    (aggs :\ JsObject()) (_ mergeAggregation _) should be (result)
  }
}

package blah.elastic

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._
import blah.elastic.AggregationMerge._

class AggregationMergeSpec extends FlatSpec with Matchers {

  "AggregationMerge" should "merge aggs" in {
    val x: JsObject = ("aggs" -> ("country" -> ("terms" -> "...")))
    val y: JsObject = ("aggs" -> ("city" -> ("terms" -> "...")))
    val z: JsObject = ("aggs" ->
                        ("country" ->
                          ("terms" -> "...") ~
                          ("aggs" ->
                            ("city" -> ("terms" -> "...")))))
    (x mergeAggregation y) should be (z)
  }

  it should "merge a list" in {
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

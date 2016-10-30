package blah.elastic

import spray.json._
import blah.json.JsonDsl._

trait AggregationMerge {
  implicit class MergeAggregation(a: JsObject) {
    def mergeAggregation(b: JsObject): JsObject = {
      val (k1, v1: JsObject) = a.fields.head
      val (k2, v2: JsObject) = v1.fields.head
      (k1 -> (k2 -> (v2 merge b)))
    }
  }
}

object AggregationMerge extends AggregationMerge

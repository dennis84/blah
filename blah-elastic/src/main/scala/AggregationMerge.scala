package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait AggregationMerge {
  implicit class MergeAggregation(a: JsObject) {
    def mergeAggregation(b: JsObject): JsObject = {
      val (k1, v1: JsObject) = a.fields.head
      val (k2, v2: JsObject) = v1.fields.head
      (k1 -> (k2 -> (v2 ~ b)))
    }
  }
}

object AggregationMerge extends AggregationMerge

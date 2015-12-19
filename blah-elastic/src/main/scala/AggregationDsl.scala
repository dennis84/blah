package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait AggregationDsl {
  def terms(k: String): JsObject =
    ("aggs" -> (k ->
      ("terms" -> ("field" -> k))
    ))

  def dateHistogram(k: String, interval: String): JsObject =
    ("aggs" -> (k ->
      ("date_histogram" -> ("field" -> k) ~ ("interval" -> interval))
    ))

  def sum(k: String): JsObject =
    ("aggs" -> (k -> ("sum" -> ("field" -> k))))

  def nest(xs: JsObject*): JsObject = nest(xs.toList)

  def nest(xs: List[JsObject]): JsObject =
    (xs :\ JsObject()) {
      case (x, a) if a.fields.isEmpty => x
      case (x, a) =>
        val (k1, v1: JsObject) = x.fields.head
        val (k2, v2: JsObject) = v1.fields.head
        (k1 -> (k2 -> (v2 merge a)))
    }
}

object AggregationDsl extends AggregationDsl

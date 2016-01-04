package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait AggregationDsl {
  def terms(k: String): JsObject =
    ("aggs" -> (k -> ("terms" -> 
      ("field" -> k) ~
      ("size" -> 0)
    )))

  def dateHistogram(k: String, interval: String): JsObject =
    ("aggs" -> (k ->
      ("date_histogram" -> ("field" -> k) ~ ("interval" -> interval))
    ))

  def sum(k: String): JsObject =
    ("aggs" -> (k -> ("sum" -> ("field" -> k))))
}

object AggregationDsl extends AggregationDsl

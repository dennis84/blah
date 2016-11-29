package blah.elastic

import spray.json._
import blah.json.JsonDsl._

trait AggregationDsl {
  def terms(k: String): JsObject =
    ("aggs" -> (k -> ("terms" ->
      ("field" -> k) ~
      ("size" -> java.lang.Integer.MAX_VALUE)
    )))

  def dateHistogram(k: String, interval: String): JsObject =
    ("aggs" -> (k ->
      ("date_histogram" -> ("field" -> k) ~ ("interval" -> interval))
    ))

  def sum(key: String, prop: String): JsObject =
    ("aggs" -> (key -> ("sum" -> ("field" -> prop))))
}

object AggregationDsl extends AggregationDsl

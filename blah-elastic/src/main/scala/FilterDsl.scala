package blah.elastic

import spray.json._
import blah.json.JsonDsl._

trait FilterDsl {
  def filter(json: JsObject): JsObject =
    ("query" -> ("filtered" -> ("filter" -> json)))

  def gte(k: String, v: JsValue): JsObject =
    filter("range" -> (k -> ("gte" -> v)))

  def lte(k: String, v: JsValue): JsObject =
    filter("range" -> (k -> ("lte" -> v)))
}

object FilterDsl extends FilterDsl

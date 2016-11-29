package blah.elastic

import spray.json._
import blah.json.JsonDsl._

trait FilterDsl {
  def filter(filter: JsObject): JsObject =
    ("query" -> ("bool" -> ("filter" -> filter)))

  def gte(k: String, v: JsValue): JsObject =
    filter("range" -> (k -> ("gte" -> v)))

  def lte(k: String, v: JsValue): JsObject =
    filter("range" -> (k -> ("lte" -> v)))
}

object FilterDsl extends FilterDsl

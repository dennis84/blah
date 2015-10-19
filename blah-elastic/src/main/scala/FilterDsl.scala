package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait FilterDsl {
  def $gte(k: String, v: String): JsObject =
    ("query" -> ("filtered" -> ("filter" -> ("range" ->
      (k -> ("gte" -> v))))))

  def $lte(k: String, v: String): JsObject =
    ("query" -> ("filtered" -> ("filter" -> ("range" ->
      (k -> ("lte" -> v))))))
}

package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait QueryDsl {
  def $match(k: String, v: String): JsObject =
    ("query" -> ("filtered" -> ("query" -> ("bool" ->
      ("must" -> List("match" -> (k -> v)))))))
}

package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait QueryDsl {
  def term(k: String, v: JsValue): JsObject =
    ("query" -> ("filtered" -> ("query" ->
      ("bool" -> ("must" -> List("term" -> (k -> v)))))))
}

object QueryDsl extends QueryDsl

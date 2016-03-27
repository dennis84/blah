package blah.elastic

import spray.json._
import blah.core.JsonDsl._

trait QueryDsl {
  def term(k: String, v: JsValue): JsObject =
    ("query" -> ("filtered" -> ("query" ->
      ("bool" -> ("must" -> List("term" -> (k -> v)))))))

  def notTerm(k: String, v: JsValue): JsObject =
    ("query" -> ("filtered" -> ("query" ->
      ("bool" -> ("must_not" -> List("term" -> (k -> v)))))))

  def matchAll(): JsObject =
    ("query" -> ("match_all" -> JsObject.empty))

  def prefix(k: String, v: JsValue): JsObject =
    ("query" -> ("filtered" -> ("query" ->
      ("bool" -> ("must" -> List("prefix" -> (k -> v)))))))
}

object QueryDsl extends QueryDsl

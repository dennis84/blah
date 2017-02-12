package blah.serving

import spray.json._
import JsonDsl._

trait QueryDsl {
  def term(k: String, v: JsValue): JsObject =
    ("query" -> ("bool" -> ("must" -> List("term" -> (k -> v)))))

  def terms(k: String, v: JsArray): JsObject =
    ("query" -> ("bool" -> ("must" -> List("terms" -> (k -> v)))))

  def notTerm(k: String, v: JsValue): JsObject =
    ("query" -> ("bool" -> ("must_not" -> List("term" -> (k -> v)))))

  def `match`(k: String, v: JsValue): JsObject =
    ("query" -> ("bool" -> ("must" -> List("match" -> (k -> v)))))

  def matchAll(): JsObject =
    ("query" -> ("match_all" -> JsObject.empty))

  def prefix(k: String, v: JsValue): JsObject =
    ("query" -> ("bool" -> ("must" -> List("prefix" -> (k -> v)))))
}

object QueryDsl extends QueryDsl

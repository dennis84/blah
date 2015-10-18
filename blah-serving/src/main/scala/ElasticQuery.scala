package blah.serving

import spray.json._
import blah.core.JsonTweaks
import blah.core.JsonDsl._

object elastic {
  object query {
    def eq(k: String, v: String): JsObject =
      ("query" -> ("filtered" -> ("query" -> ("bool" ->
        ("must" -> List("match" -> (k -> v)))
      ))))

    def gte(k: String, v: String) =
      JsObject("query" ->
        JsObject("filtered" ->
          JsObject("filter" ->
            JsObject("range" ->
              JsObject(k -> JsObject("gte" -> JsString(v)))
            ))))

    def lte(k: String, v: String) =
      JsObject("query" ->
        JsObject("filtered" ->
          JsObject("filter" ->
            JsObject("range" ->
              JsObject(k -> JsObject("lte" -> JsString(v)))
            ))))
  }

  object aggs {
    def term(k: String) =
      JsObject("aggs" -> JsObject(k -> JsObject(
        "terms" -> JsObject("field" -> JsString(k)),
        "aggs" -> JsObject(
          "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))))
      )))

    def date(k: String, interval: String) =
      JsObject("aggs" -> JsObject(k -> JsObject(
        "date_histogram" -> JsObject(
          "field" -> JsString(k),
          "interval" -> JsString(interval)),
        "aggs" -> JsObject(
          "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))))
      )))
  }
}

trait ElasticQuery extends JsonTweaks {

  def mergeAggs(xs: List[JsObject]): JsObject =
    xs.foldRight(JsObject()) {
      case (x, a) if a.fields.isEmpty => x
      case (x, a) => {
        val (k1, v1: JsObject) = x.fields.head
        val (k2, v2: JsObject) = v1.fields.head
        JsObject(k1 -> JsObject(k2 -> (v2 merge a)))
      }
    }
}

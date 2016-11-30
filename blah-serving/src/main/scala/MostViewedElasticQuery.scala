package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}

object MostViewedElasticQuery {
  def apply(query: MostViewedQuery): JsValue =
    q.term("collection", query.collection) merge
    ("aggs" -> ("item" ->
      ("terms" ->
        ("field" -> "item.keyword") ~
        ("size" -> query.limit.getOrElse(java.lang.Integer.MAX_VALUE)) ~
        ("order" -> ("count" -> "desc"))) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    ))
}

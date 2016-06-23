package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}

object MostViewedElasticQuery {
  def apply(query: MostViewedQuery): JsValue =
    q.term("collection", query.collection) merge
    ("aggs" -> ("item" ->
      ("terms" ->
        ("field" -> "item") ~
        ("size" -> query.limit.getOrElse(100)) ~
        ("order" -> ("count" -> "desc"))) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    ))
}

package blah.serving

import spray.json._
import JsonDsl._

object MostViewedElasticQuery {
  def apply(query: MostViewedQuery): JsValue =
    QueryDsl.term("collection", query.collection) merge ("aggs" -> ("item" ->
      ("terms" ->
        ("field" -> "item.keyword") ~
        ("size" -> query.limit.getOrElse(java.lang.Integer.MAX_VALUE)) ~
        ("order" -> ("count" -> "desc"))) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    ))
}

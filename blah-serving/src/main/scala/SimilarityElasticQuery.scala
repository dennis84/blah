package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}

object SimilarityElasticQuery {
  def apply(query: SimilarityQuery): JsValue =
    query.collection map { coll =>
      q.term("collection", coll) merge
      q.terms("item", query.items)
    } getOrElse {
      q.terms("item", query.items)
    } ~
    ("aggs" -> ("sims" ->
      ("nested" -> ("path", "similarities")) ~
      ("aggs" -> ("items" ->
        ("top_hits" ->
          ("sort" -> List("similarities.score" -> ("order" -> "desc"))) ~
          ("_source" -> ("include" -> List("item", "score"))) ~
          ("size" -> query.limit.getOrElse(10))
        )))))
}

package blah.serving

import spray.json._
import JsonDsl._

object SimilarityElasticQuery {
  def apply(query: SimilarityQuery): JsValue =
    query.collection map { coll =>
      QueryDsl.term("collection", coll) merge
      QueryDsl.terms("item", query.items)
    } getOrElse {
      QueryDsl.terms("item", query.items)
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

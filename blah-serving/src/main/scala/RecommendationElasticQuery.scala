package blah.serving

import spray.json._
import JsonDsl._

object RecommendationElasticQuery {
  def apply(query: RecommendationQuery): JsValue =
    query.collection map { coll =>
      QueryDsl.term("user", query.user) merge
      QueryDsl.term("collection", coll)
    } getOrElse {
      QueryDsl.term("user", query.user)
    }
}

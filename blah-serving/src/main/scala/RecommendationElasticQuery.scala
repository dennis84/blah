package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}

object RecommendationElasticQuery {
  def apply(query: RecommendationQuery): JsValue =
    query.collection map { coll =>
      q.term("user", query.user) merge
      q.term("collection", coll)
    } getOrElse {
      q.term("user", query.user)
    }
}

package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}

object CollectionElasticQuery {
  def apply(query: CollectionQuery): JsValue =
    q.term("name", query.name)
}

package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}

object FunnelElasticQuery {
  def apply(query: FunnelQuery): JsValue =
    q.term("name", query.name)
}

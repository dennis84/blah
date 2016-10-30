package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}

object FunnelElasticQuery {
  def apply(query: FunnelQuery): JsValue =
    q.term("name", query.name)
}

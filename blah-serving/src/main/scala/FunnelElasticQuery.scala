package blah.serving

import spray.json._
import JsonDsl._

object FunnelElasticQuery {
  def apply(query: FunnelQuery): JsValue =
    QueryDsl.term("name", query.name)
}

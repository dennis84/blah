package blah.serving

import spray.json._
import JsonDsl._

object CollectionElasticQuery {
  def apply(query: CollectionQuery): JsValue =
    QueryDsl.term("name", query.name)
}

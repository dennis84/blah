package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{AggregationDsl => a}

object UserElasticQuery {
  private def filterBy(xs: Map[String, String]): JsObject = JsObject()

  private def groupBy(xs: List[String]): JsObject =
    a.nest(xs.collect {
      case "country" => a.terms("country")
    })

  def filtered(q: UserQuery): JsObject = JsObject()

  def grouped(q: UserQuery): JsObject = List(
    q.filterBy map (filterBy),
    q.groupBy map (groupBy)
  ).flatten reduceOption {
    (a,b) => a merge b
  } getOrElse JsObject()
}

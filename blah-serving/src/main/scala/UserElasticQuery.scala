package blah.serving

import spray.json._
import blah.elastic.{AggregationDsl => a}

object UserElasticQuery {
  private def groupBy(xs: List[String]): JsObject =
    a.nest(xs.collect {
      case "country" => a.terms("country")
    })

  def apply(q: UserQuery): JsObject = q match {
    case UserQuery(None, Some(groups)) => groupBy(groups)
    case UserQuery(Some(filters), Some(groups)) => groupBy(groups)
    case _ => JsObject()
  }
}

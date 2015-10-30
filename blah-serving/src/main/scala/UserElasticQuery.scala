package blah.serving

import spray.json._
import blah.elastic.{AggregationDsl => a}

object UserElasticQuery {
  private def groupBy(xs: List[String]): JsObject =
    a.nest(xs.collect {
      case "country" => a.terms("country")
    })

  def apply(q: Query): JsObject = q match {
    case Query(None, Some(groups)) => groupBy(groups)
    case Query(Some(filters), Some(groups)) => groupBy(groups)
    case _ => JsObject()
  }
}

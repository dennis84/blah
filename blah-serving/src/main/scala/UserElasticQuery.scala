package blah.serving

import spray.json._
import blah.elastic.{AggregationDsl => a}
import blah.elastic.AggregationMerge._

object UserElasticQuery {
  private def groupBy(xs: List[String]): JsObject =
    (xs.collect {
      case "country" => a.terms("country")
    } :\ JsObject()) (_ mergeAggregation _)

  def apply(q: Query): JsObject = q match {
    case Query(None, Some(groups)) => groupBy(groups)
    case Query(Some(filters), Some(groups)) => groupBy(groups)
    case _ => JsObject()
  }
}

package blah.serving

import spray.json._
import JsonDsl._
import AggregationMerge._

object UserElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("date.from", "gte", value) => FilterDsl.gte("date", value)
    case Filter("date.to", "lte", value)   => FilterDsl.lte("date", value)
    case Filter("user", "eq", value)       => QueryDsl.term("user", value)
    case Filter("user", "contains", value) => QueryDsl.prefix("user", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    (xs.collect {
      case "country" => AggregationDsl.terms("country")
    } :\ JsObject()) (_ mergeAggregation _)

  def apply(query: UserQuery): JsValue = (query match {
    case UserQuery(Some(filters), None) => filterBy(filters)
    case UserQuery(Some(filters), Some(Nil)) => filterBy(filters)
    case UserQuery(None, Some(groups)) => groupBy(groups)
    case UserQuery(Some(filters), Some(groups)) =>
      filterBy(filters) merge groupBy(groups)
    case _ => JsObject()
  }) match {
    case JsObject.empty => QueryDsl.matchAll
    case x => x
  }
}

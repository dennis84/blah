package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}
import blah.elastic.{FilterDsl => f}
import blah.elastic.{AggregationDsl => a}
import blah.elastic.AggregationMerge._

object UserElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("date.from", "gte", value) => f.gte("date", value)
    case Filter("date.to", "lte", value)   => f.lte("date", value)
    case Filter("user", "eq", value)       => q.term("user", value)
    case Filter("user", "contains", value) => q.prefix("user", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    (xs.collect {
      case "country" => a.terms("country")
    } :\ JsObject()) (_ mergeAggregation _)

  def apply(query: UserQuery): JsValue = (query match {
    case UserQuery(Some(filters), None) => filterBy(filters)
    case UserQuery(Some(filters), Some(Nil)) => filterBy(filters)
    case UserQuery(None, Some(groups)) => groupBy(groups)
    case UserQuery(Some(filters), Some(groups)) =>
      filterBy(filters) merge groupBy(groups)
    case _ => JsObject()
  }) match {
    case JsObject.empty => q.matchAll
    case x => x
  }
}

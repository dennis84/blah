package blah.serving

import spray.json._
import JsonDsl._
import AggregationMerge._

object CountElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("item", "eq", value)                      => QueryDsl.term("item", value)
    case Filter("item", "ne", value)                      => QueryDsl.notTerm("item", value)
    case Filter("user_agent.device.family", "eq", value)  => QueryDsl.term("deviceFamily", value)
    case Filter("user_agent.device.family", "ne", value)  => QueryDsl.notTerm("deviceFamily", value)
    case Filter("user_agent.browser.family", "eq", value) => QueryDsl.`match`("browserFamily", value)
    case Filter("user_agent.browser.family", "ne", value) => QueryDsl.notTerm("browserFamily", value)
    case Filter("user_agent.browser.major", "eq", value)  => QueryDsl.term("browserMajor", value)
    case Filter("user_agent.browser.major", "ne", value)  => QueryDsl.notTerm("browserMajor", value)
    case Filter("user_agent.os.family", "eq", value)      => QueryDsl.term("osFamily", value)
    case Filter("user_agent.os.family", "ne", value)      => QueryDsl.notTerm("osFamily", value)
    case Filter("user_agent.os.major", "eq", value)       => QueryDsl.term("osMajor", value)
    case Filter("user_agent.os.major", "ne", value)       => QueryDsl.notTerm("osMajor", value)
    case Filter("user_agent.platform", "eq", value)       => QueryDsl.term("platform", value)
    case Filter("user_agent.platform", "ne", value)       => QueryDsl.notTerm("platform", value)
    case Filter("date.from", "gte", value)                => FilterDsl.gte("date", value)
    case Filter("date.to", "lte", value)                  => FilterDsl.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    (((xs collectFirst {
      case "date.hour"  => AggregationDsl.dateHistogram("date", "hour")
      case "date.month" => AggregationDsl.dateHistogram("date", "month")
      case "date.year"  => AggregationDsl.dateHistogram("date", "year")
    } getOrElse AggregationDsl.dateHistogram("date", "day")) :: xs.collect {
      case "user_agent.browser.family" => AggregationDsl.terms("browserFamily")
      case "user_agent.browser.major"  => AggregationDsl.terms("browserMajor")
      case "user_agent.os.family"      => AggregationDsl.terms("osFamily")
      case "user_agent.device.family"  => AggregationDsl.terms("deviceFamily")
      case "user_agent.platform"       => AggregationDsl.terms("platform")
    } map (_ mergeAggregation AggregationDsl.sum("count", "count"))) :\ JsObject()) {
      _ mergeAggregation _
    }
  
  def apply(query: CountQuery) =
    QueryDsl.term("collection", query.collection) merge (query match {
      case CountQuery(coll, Some(filters), None) =>
        filterBy(filters) merge AggregationDsl.sum("count", "count")
      case CountQuery(collection, None, Some(groups)) => groupBy(groups)
      case CountQuery(coll, Some(filters), Some(groups)) =>
        filterBy(filters) merge groupBy(groups)
      case _ => AggregationDsl.sum("count", "count")
    })
}

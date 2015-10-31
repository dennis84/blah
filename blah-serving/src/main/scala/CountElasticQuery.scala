package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}
import blah.elastic.{FilterDsl => f}
import blah.elastic.{AggregationDsl => a}

object CountElasticQuery {

  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("page", "eq", value)                      => q.term("page", value)
    case Filter("user_agent.device.family", "eq", value)  => q.term("deviceFamily", value)
    case Filter("user_agent.browser.family", "eq", value) => q.term("browserFamily", value)
    case Filter("user_agent.browser.major", "eq", value)  => q.term("browserMajor", value)
    case Filter("user_agent.browser.minor", "eq", value)  => q.term("browserMinor", value)
    case Filter("user_agent.browser.patch", "eq", value)  => q.term("browserPatch", value)
    case Filter("user_agent.os.family", "eq", value)      => q.term("osFamily", value)
    case Filter("user_agent.os.major", "eq", value)       => q.term("osMajor", value)
    case Filter("user_agent.os.minor", "eq", value)       => q.term("osMinor", value)
    case Filter("user_agent.os.patch", "eq", value)       => q.term("osPatch", value)
    case Filter("user_agent.platform", "ne", value)       => q.notTerm("platform", value)
    case Filter("date.from", "gte", value)                => f.gte("date", value)
    case Filter("date.to", "lte", value)                  => f.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    a.nest(((xs collectFirst {
      case "date.hour"  => a.dateHistogram("date", "hour")
      case "date.month" => a.dateHistogram("date", "month")
      case "date.year"  => a.dateHistogram("date", "year")
    } getOrElse a.dateHistogram("date", "day")) :: xs.collect {
      case "user_agent.browser.family" => a.terms("browserFamily")
      case "user_agent.browser.major"  => a.terms("browserMajor")
      case "user_agent.os.family"      => a.terms("osFamily")
      case "user_agent.device.family"  => a.terms("deviceFamily")
      case "user_agent.platform"       => a.terms("platform")
    }) map (x => a.nest(x, a.sum("count"))))

  def apply(q: Query) = q match {
    case Query(Some(filters), None) =>
      filterBy(filters) merge a.sum("count")
    case Query(None, Some(groups)) => groupBy(groups)
    case Query(Some(filters), Some(groups)) =>
      filterBy(filters) merge groupBy(groups)
    case _ => a.sum("count")
  }
}

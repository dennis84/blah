package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}
import blah.elastic.{FilterDsl => f}
import blah.elastic.{AggregationDsl => a}

object CountElasticQuery {

  private def filterBy(xs: Map[String, String]) = xs collect {
    case ("page", value)                      => q.term("page", value)
    case ("user_agent.device.family", value)  => q.term("deviceFamily", value)
    case ("user_agent.browser.family", value) => q.term("browserFamily", value)
    case ("user_agent.browser.major", value)  => q.term("browserMajor", value)
    case ("user_agent.browser.minor", value)  => q.term("browserMinor", value)
    case ("user_agent.browser.patch", value)  => q.term("browserPatch", value)
    case ("user_agent.os.family", value)      => q.term("osFamily", value)
    case ("user_agent.os.major", value)       => q.term("osMajor", value)
    case ("user_agent.os.minor", value)       => q.term("osMinor", value)
    case ("user_agent.os.patch", value)       => q.term("osPatch", value)
    case ("date.from", value)                 => f.gte("date", value)
    case ("date.to", value)                   => f.lte("date", value)
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
    }) map (x => a.nest(x, a.sum("count"))))

  def apply(q: CountQuery) = q match {
    case CountQuery(Some(filters), None) =>
      filterBy(filters) merge a.sum("count")
    case CountQuery(None, Some(groups)) => groupBy(groups)
    case CountQuery(Some(filters), Some(groups)) =>
      filterBy(filters) merge groupBy(groups)
    case _ => a.sum("count")
  }
}

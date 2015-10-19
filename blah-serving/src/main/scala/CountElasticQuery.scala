package blah.serving

import spray.json._
import blah.core.JsonTweaks._
import blah.elastic.FullDsl._

object CountElasticQuery {

  private def filterBy(xs: Map[String, String]) = xs collect {
    case ("page", value)                      => $match("page", value)
    case ("user_agent.device.family", value)  => $match("deviceFamily", value)
    case ("user_agent.browser.family", value) => $match("browserFamily", value)
    case ("user_agent.browser.major", value)  => $match("browserMajor", value)
    case ("user_agent.browser.minor", value)  => $match("browserMinor", value)
    case ("user_agent.browser.patch", value)  => $match("browserPatch", value)
    case ("user_agent.os.family", value)      => $match("osFamily", value)
    case ("user_agent.os.major", value)       => $match("osMajor", value)
    case ("user_agent.os.minor", value)       => $match("osMinor", value)
    case ("user_agent.os.patch", value)       => $match("osPatch", value)
    case ("date.from", value)                 => $gte("date", value)
    case ("date.to", value)                   => $lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    nest(
      ((xs collectFirst {
        case "date.hour" => date("date", "hour")
        case "date.month" => date("date", "month")
        case "date.year" => date("date", "year")
      } getOrElse date("date", "day")) :: xs.collect {
        case "user_agent.browser.family" => term("browserFamily")
        case "user_agent.browser.major" => term("browserMajor")
        case "user_agent.os.family" => term("osFamily")
      }) map (x => nest(x, sum("count")))
    )

  def filtered(q: CountQuery) =
    q.filterBy map {
      filters => filterBy(filters) merge sum("count")
    } getOrElse sum("count")

  def grouped(q: CountQuery) = List(
    q.filterBy map (filterBy),
    q.groupBy map (groupBy)
  ).flatten reduceOption {
    (a,b) => a merge b
  } getOrElse JsObject()
}

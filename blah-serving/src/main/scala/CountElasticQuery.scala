package blah.serving

import spray.json._

object CountElasticQuery extends ElasticQuery {
  import elastic._

  private def filterBy(xs: Map[String, String]) = xs collect {
    case ("page", value)                      => query.eq("page", value)
    case ("user_agent.device.family", value)  => query.eq("deviceFamily", value)
    case ("user_agent.browser.family", value) => query.eq("browserFamily", value)
    case ("user_agent.browser.major", value)  => query.eq("browserMajor", value)
    case ("user_agent.browser.minor", value)  => query.eq("browserMinor", value)
    case ("user_agent.browser.patch", value)  => query.eq("browserPatch", value)
    case ("user_agent.os.family", value)      => query.eq("osFamily", value)
    case ("user_agent.os.major", value)       => query.eq("osMajor", value)
    case ("user_agent.os.minor", value)       => query.eq("osMinor", value)
    case ("user_agent.os.patch", value)       => query.eq("osPatch", value)
    case ("date.from", value)                 => query.gte("date", value)
    case ("date.to", value)                   => query.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def groupBy(xs: List[String]): JsObject =
    mergeAggs(
      (xs collectFirst {
        case "date.hour" => aggs.date("date", "hour")
        case "date.month" => aggs.date("date", "month")
        case "date.year" => aggs.date("date", "year")
      } getOrElse aggs.date("date", "day")) :: xs.collect {
        case "user_agent.browser.family" => aggs.term("browserFamily")
        case "user_agent.browser.major" => aggs.term("browserMajor")
        case "user_agent.os.family" => aggs.term("osFamily")
      }
    )

  private def mkCountAgg =
    JsObject("aggs" -> JsObject("count" -> JsObject(
      "sum" -> JsObject("field" -> JsString("count")))))

  def filtered(q: CountQuery) =
    q.filterBy map {
      filters => filterBy(filters) merge mkCountAgg
    } getOrElse mkCountAgg

  def grouped(q: CountQuery) = List(
    q.filterBy map (filterBy),
    q.groupBy map (groupBy)
  ).flatten reduceOption {
    (a,b) => a merge b
  } getOrElse JsObject()
}

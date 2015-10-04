package blah.serving

import spray.json._
import blah.core.JsonTweaks._

object CountQueryToEs {

  private def mustMatch(k: String, v: String) =
    JsObject("query" ->
      JsObject("filtered" ->
        JsObject("query" ->
          JsObject("bool" ->
            JsObject("must" -> JsArray(
              JsObject("match" -> JsObject(k -> JsString(v)))
            ))))))

  private def filterBy(xs: Map[String, String]): JsObject = xs collect {
    case ("page", value) => mustMatch("page", value)
    case ("user_agent.device.family", value: String)  => mustMatch("deviceFamily", value)
    case ("user_agent.browser.family", value: String) => mustMatch("browserFamily", value)
    case ("user_agent.browser.major", value: String) => mustMatch("browserMajor", value)
    case ("user_agent.browser.minor", value: String) => mustMatch("browserMinor", value)
    case ("user_agent.browser.patch", value: String) => mustMatch("browserPatch", value)
    case ("user_agent.os.family", value: String) => mustMatch("osFamily", value)
    case ("user_agent.os.major", value: String) => mustMatch("osMajor", value)
    case ("user_agent.os.minor", value: String) => mustMatch("osMinor", value)
    case ("user_agent.os.patch", value: String) => mustMatch("osPatch", value)
    case ("date.from", value: String) =>
      JsObject("query" -> JsObject("filtered" -> JsObject("filter" ->
        JsObject("range" -> JsObject("date" -> JsObject("gte" -> JsString(value)))))))
    case ("date.to", value: String) =>
      JsObject("query" -> JsObject("filtered" -> JsObject("filter" ->
        JsObject("range" -> JsObject("date" -> JsObject("lte" -> JsString(value)))))))
  } reduceOption (_ merge _) getOrElse JsObject()

  private def mergeAggs(xs: List[JsObject]): JsObject =
    xs.foldRight(JsObject()) {
      case (x, a) if a.fields.isEmpty => x
      case (x, a) => {
        val (key, value: JsObject) = x.fields.head
        JsObject(key -> (value merge JsObject("aggs" -> a)))
      }
    }

  private def withCountAgg(xs: List[JsObject]): List[JsObject] = xs map {
    case x if x.fields == xs.last.fields => {
      val (key, value: JsObject) = x.fields.head
      JsObject(key -> (value merge mkCountAgg))
    }
    case x => x
  }

  private def groupBy(xs: List[String]): JsObject = JsObject(
    "aggs" -> mergeAggs(withCountAgg(
      JsObject("date" -> JsObject("date_histogram" -> getDateAgg(xs))) :: xs.collect {
        case "user_agent.browser.family" =>
          JsObject("browserFamily" -> JsObject("terms" -> JsObject("field" -> JsString("browserFamily"))))
        case "user_agent.browser.major" =>
          JsObject("browserMajor" -> JsObject("terms" -> JsObject("field" -> JsString("browserMajor"))))
        case "user_agent.os.family" =>
          JsObject("osFamily" -> JsObject("terms" -> JsObject("field" -> JsString("osFamily"))))
      }
    )))

  private def getDateAgg(xs: List[String]) = xs collectFirst {
    case "date.hour" => JsObject("field" -> JsString("date"), "interval" -> JsString("hour"))
    case "date.month" => JsObject("field" -> JsString("date"), "interval" -> JsString("month"))
    case "date.year" => JsObject("field" -> JsString("date"), "interval" -> JsString("year"))
  } getOrElse JsObject("field" -> JsString("date"), "interval" -> JsString("day"))

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

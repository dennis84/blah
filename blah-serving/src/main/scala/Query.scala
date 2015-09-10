package blah.serving

import spray.json._
import DefaultJsonProtocol._
import blah.core.JsonTweaks._

/**
 * ## Collecting Data:
 *
 * ```json
 * POST <api>/events
 * {
 *   "name": "view",
 *   "props": {
 *     "page": "home",
 *     "user": "username",
 *     "userAgent": "Name/Version (Comment)"
 *   }
 * }
 *
 * ## Query Data:
 *
 * POST <serving>/events/count
 * {
 *   "filterBy": {
 *     "page": "home",
 *     "user_agent.device.family": "iPhone"
 *   }
 * }
 *
 * {
 *   "count": 1
 * }
 *
 * POST <serving>/events/count
 * {
 *   "filterBy": {},
 *   "groupBy": [
 *     "user_agent.device.family",
 *     "date.hour"
 *   ]
 * }
 *
 * [{
 *   "count": 1
 *   "date": "datetime",
 *   "user_agent.device.family": "Android"
 * }, {
 *   "count": 1
 *   "date": "datetime",
 *   "user_agent.device.family": "iPhone"
 * }]
 * ```
 */

case class Query(
  filterBy: Option[Map[String, String]] = None,
  groupBy: Option[List[String]] = None) {

  private def mustMatch(k: String, v: String) =
    JsObject("query" -> JsObject("bool" -> JsObject("must" -> JsArray(JsObject(
      "match" -> JsObject(k -> JsString(v))
    )))))

  def createFilterByQuery: JsObject = filterBy.map(_.collect {
    case ("page", value: String)                      => mustMatch("page", value)
    case ("user_agent.device.family", value: String)  => mustMatch("deviceFamily", value)
    case ("user_agent.browser.family", value: String) => mustMatch("browserFamily", value)
    case ("user_agent.browser.major", value: String)  => mustMatch("browserMajor", value)
    case ("user_agent.browser.minor", value: String)  => mustMatch("browserMinor", value)
    case ("user_agent.browser.patch", value: String)  => mustMatch("browserPatch", value)
    case ("user_agent.os.family", value: String)      => mustMatch("osFamily", value)
    case ("user_agent.os.major", value: String)       => mustMatch("osMajor", value)
    case ("user_agent.os.minor", value: String)       => mustMatch("osMinor", value)
    case ("user_agent.os.patch", value: String)       => mustMatch("osPatch", value)
    case ("date.from", value: String) =>
      JsObject("filter" -> JsObject("range" -> JsObject("date" -> JsObject("gte" -> JsString(value)))))
    case ("date.to", value: String) =>
      JsObject("filter" -> JsObject("range" -> JsObject("date" -> JsObject("lte" -> JsString(value)))))
  }.reduceOption(_ merge _)).flatten getOrElse JsObject()

  def createGroupByQuery: JsObject = groupBy.map(xs => (List(
    JsObject("size" -> JsNumber(0)),
    JsObject("aggs" -> JsObject("date" -> JsObject(
      "date_histogram" -> JsObject(
        "field"    -> JsString("date"),
        "interval" -> JsString("day")
      ),
      "aggs" -> createNestedAggs
    )))
  ) ++ xs.collect {
    case "date.hour" =>
      JsObject("aggs" -> JsObject("date" -> JsObject(
        "date_histogram" -> JsObject(
          "field"    -> JsString("date"),
          "interval" -> JsString("hour")
        ),
        "aggs" -> createNestedAggs
      )))
    case "date.month" =>
      JsObject("aggs" -> JsObject("date" -> JsObject(
        "date_histogram" -> JsObject(
          "field"    -> JsString("date"),
          "interval" -> JsString("month")
        ),
        "aggs" -> createNestedAggs
      )))
  }).reduceOption(_ merge _)).flatten getOrElse JsObject()

  def createNestedAggs = groupBy map (xs => mergeAggs(xs.collect {
    case "user_agent.browser.family" =>
      JsObject("browserFamily" -> JsObject("terms" -> JsObject("field" -> JsString("browserFamily"))))
    case "user_agent.browser.major" =>
      JsObject("browserMajor" -> JsObject("terms" -> JsObject("field" -> JsString("browserMajor"))))
    case "user_agent.os.family" =>
      JsObject("osFamily" -> JsObject("terms" -> JsObject("field" -> JsString("osFamily"))))
  })) getOrElse JsObject()

  def mergeAggs(xs: List[JsObject]): JsObject =
    xs.foldRight(JsObject()) {
      case (v,a) if a.fields.isEmpty => v
      case (v,a) => v.fields.toList match {
        case (k, v: JsObject) :: rest =>
          JsObject(rest.toMap + (k -> JsObject(v.fields ++ Map("aggs" -> JsObject(a.fields)))))
        case _ => a
      }
    }

  def toEs = JsObject(
    createFilterByQuery.fields ++
    createGroupByQuery.fields
  ).compactPrint
}

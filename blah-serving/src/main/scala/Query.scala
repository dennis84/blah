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
    Map("query" -> Map("bool" -> Map("must" -> List(Map(
      "match" -> Map(k -> v)
    ))))).toJson.asJsObject

  def toEs = filterBy map (_.collect {
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
      Map("filter" -> Map("range" -> Map("date" -> Map("gte" -> value)))).toJson.asJsObject
    case ("date.to", value: String) =>
      Map("filter" -> Map("range" -> Map("date" -> Map("lte" -> value)))).toJson.asJsObject
  }.reduce(_ merge _).compactPrint) getOrElse "{}"
}

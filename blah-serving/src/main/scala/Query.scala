package blah.serving

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
  groupBy: Option[List[String]] = None)

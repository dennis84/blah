package blah.serving

import spray.json._

case class CountResult(count: Long)

case class GroupedCountResult(
  items: List[Map[String, JsValue]] = Nil)

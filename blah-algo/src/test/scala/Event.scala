package blah.algo

import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.json.TimeJsonProtocol

case class Event(
  id: String,
  collection: String,
  date: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
  props: Map[String, JsValue] = Map.empty)

object EventJsonProtocol extends TimeJsonProtocol {
  implicit val eventFmt = jsonFormat4(Event)
}

package blah.api

import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._

case class Event(
  id: String,
  collection: String,
  date: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
  props: Map[String, JsValue] = Map.empty)

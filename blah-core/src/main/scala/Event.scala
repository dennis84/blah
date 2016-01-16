package blah.core

import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._

case class Event(
  id: String,
  name: String,
  date: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
  props: Map[String, JsValue] = Map.empty)

case class ViewProps(
  page: String,
  user: Option[String] = None,
  userAgent: Option[String] = None)
case class ViewEvent(
  id: String,
  name: String,
  date: ZonedDateTime,
  props: ViewProps)

case class UserProps(
  user: String,
  ip: Option[String] = None)
case class UserEvent(
  id: String,
  date: ZonedDateTime,
  props: UserProps)

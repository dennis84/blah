package blah.core

import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._

case class Event(
  id: String,
  collection: String,
  date: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
  props: Map[String, JsValue] = Map.empty)

case class ViewProps(
  item: String,
  title: Option[String] = None,
  referrer: Option[String] = None,
  user: Option[String] = None,
  userAgent: Option[String] = None)
case class ViewEvent(
  id: String,
  collection: String,
  date: ZonedDateTime,
  props: ViewProps)

case class UserProps(
  user: String,
  title: Option[String] = None,
  item: Option[String] = None,
  ip: Option[String] = None)
case class UserEvent(
  id: String,
  date: String,
  props: UserProps)

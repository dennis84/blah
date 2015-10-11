package blah.core

import spray.json._
import org.joda.time.DateTime

case class Event(
  id: String,
  name: String,
  date: DateTime,
  props: Map[String, JsValue] = Map.empty)

case class ViewProps(
  page: String,
  user: String,
  ip: Option[String] = None,
  userAgent: Option[String] = None)

case class ViewEvent(
  id: String,
  name: String,
  date: DateTime,
  props: ViewProps)

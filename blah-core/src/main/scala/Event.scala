package blah.core

import spray.json._
import com.github.nscala_time.time.Imports._

case class Event(
  id: String,
  name: String,
  date: DateTime = DateTime.now,
  props: Map[String, JsValue] = Map.empty)

case class ViewProps(
  page: String,
  user: Option[String] = None,
  userAgent: Option[String] = None)
case class ViewEvent(
  id: String,
  name: String,
  date: DateTime,
  props: ViewProps)

case class UserProps(
  user: String,
  ip: Option[String] = None)
case class UserEvent(
  id: String,
  date: DateTime,
  props: UserProps)

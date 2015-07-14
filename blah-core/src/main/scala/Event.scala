package blah.core

import spray.json._
import org.joda.time.DateTime

case class Event(
  val id: String,
  val name: String,
  val date: DateTime,
  val props: Map[String, JsValue] = Map.empty)

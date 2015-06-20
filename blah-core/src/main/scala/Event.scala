package blah.core

import spray.json._

case class Event(
  val id: String,
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

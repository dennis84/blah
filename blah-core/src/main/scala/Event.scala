package blah.core

import spray.json._
import akka.http.scaladsl.model.DateTime

case class Event(
  val id: String,
  val name: String,
  val date: DateTime,
  val props: Map[String, JsValue] = Map.empty)

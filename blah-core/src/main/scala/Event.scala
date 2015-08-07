package blah.core

import spray.json._
import org.joda.time.DateTime

case class Event(
  id: String,
  name: String,
  date: DateTime,
  props: Map[String, JsValue] = Map.empty) {

  def prop[A : JsonReader](name: String): Option[A] =
    props.get(name) map (_.convertTo[A])
}

case class ViewProps(
  event: String,
  user: String)

case class ViewEvent(
  id: String,
  name: String,
  date: DateTime,
  props: ViewProps)

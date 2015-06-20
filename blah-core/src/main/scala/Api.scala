package blah.core

import akka.actor._
import spray.json._

case class EventReq(
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

class Api extends Actor {

  def receive = {
    case EventReq(name, props) =>
      sender ! Event(Id.generate, name, props)
  }
}

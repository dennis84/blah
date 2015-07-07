package blah.core

import scala.concurrent._
import akka.actor._
import spray.json._
import akka.pattern.pipe

case class CreateEvent(
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

class Api extends Actor {
  implicit val executor = context.dispatcher

  def receive = {
    case CreateEvent(name, props) =>
      sender ! Event(Id.generate, name, props)
  }
}

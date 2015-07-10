package blah.core

import scala.concurrent._
import akka.actor._
import spray.json._
import akka.pattern.pipe

case class CreateEvent(
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

class Api(producer: Producer[String]) extends Actor with JsonProtocol {
  implicit val executor = context.dispatcher

  def receive = {
    case CreateEvent(name, props) => {
      val evt = Event(Id.generate, name, props)
      producer.send(evt.toJson.compactPrint)
      sender ! evt
    }
  }
}

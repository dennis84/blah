package blah.core

import scala.concurrent._
import akka.actor._
import spray.json._
import akka.pattern.pipe

case class CreateEvent(
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

case object FindEvents

class Api(repo: EventRepo) extends Actor {
  implicit val executor = context.dispatcher

  def receive = {
    case CreateEvent(name, props) => {
      val event = Event(Id.generate, name, props)
      (for {
        _ <- repo insert event
      } yield event) pipeTo sender
    }

    case FindEvents => repo.findAll pipeTo sender
  }
}

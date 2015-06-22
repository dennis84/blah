package blah.core

import scala.concurrent._
import akka.actor._
import spray.json._
import akka.pattern.pipe

case class EventReq(
  val name: String,
  val props: Map[String, JsValue] = Map.empty)

case object FindAll

class Api(repo: EventRepo) extends Actor {
  implicit val executor = context.dispatcher

  def receive = {
    case EventReq(name, props) => {
      val event = Event(Id.generate, name, props)
      (for {
        _ <- repo insert event
      } yield event) pipeTo sender
    }

    case FindAll => repo.findAll pipeTo sender
  }
}

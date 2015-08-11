package blah.api

import java.util.UUID
import akka.actor._
import akka.pattern.pipe
import spray.json._
import com.github.nscala_time.time.Imports._
import blah.core.{Event, Producer}

class EventApi(
  repo: EventRepo,
  producer: Producer[String]
) extends Actor with ApiJsonProtocol {
  implicit val executor = context.dispatcher

  def receive = {
    case EventApi.Create(name, props) =>
      val evt = Event(UUID.randomUUID.toString, name, DateTime.now, props)
      repo.insert(evt) map { e =>
        EventApi.Message("Event successfully created.")
      } pipeTo sender
  }
}

object EventApi {
  case class Create(
    name: String,
    props: Map[String, JsValue] = Map.empty)
  case class Message(text: String)
}

package blah.api

import java.util.UUID
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import spray.json._
import blah.core.Event
import Directives._

class Service(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer,
  log: LoggingAdapter
) extends ApiJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  val route = path("events" / Segment) { name =>
    (post & entity(as[Map[String, JsValue]])) { props =>
      val evt = Event(UUID.randomUUID.toString, name, props = props)
      env.hdfs ! HdfsWriter.Write(evt)
      (env.producer send evt.toJson.compactPrint) onComplete {
        case Success(_) => log.debug("Message sent successfully.")
        case Failure(_) => log.debug("Message could not be sent.")
      }

      complete(OK -> Service.Message("Event successfully created."))
    }
  }
}

object Service {
  case class Message(text: String)
}

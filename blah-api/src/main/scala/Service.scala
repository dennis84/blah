package blah.api

import java.util.UUID
import scala.util.{Try, Success, Failure}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import com.github.nscala_time.time.Imports._
import spray.json._
import blah.core.Event
import Directives._

class Service(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer,
  timeout: Timeout
) extends ApiJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  val route = pathPrefix("events") {
    (post & entity(as[Service.Create])) { req =>
      complete {
        val evt = Event(
          UUID.randomUUID.toString, req.name,
          DateTime.now, req.props)
        Try(env.producer.send(evt.toJson.compactPrint)) match {
          case Success(_) =>
            Created -> Service.Message("Event successfully created.")
          case Failure(e) =>
            InternalServerError -> Service.Message("Message could not be sent")
        }
      }
    }
  }
}

object Service {
  case class Create(
    name: String,
    props: Map[String, JsValue] = Map.empty)
  case class Message(text: String)
}

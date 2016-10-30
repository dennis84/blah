package blah.api

import java.util.UUID
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import spray.json._
import Directives._
import ApiJsonProtocol._

class Service(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer,
  log: LoggingAdapter
) extends SprayJsonSupport {
  import system.dispatcher

  val route =
    (get & path("")) {
      complete(Message("OK"))
    } ~
    path("events" / Segment) { collection =>
      (post & entity(as[Map[String, JsValue]])) { props =>
        val evt = Event(UUID.randomUUID.toString, collection, props = props)
        env.hdfs ! HdfsWriter.Write(evt)
        env.producer ! evt
        complete(OK -> Message("Event successfully created."))
      }
    }
}

package blah.api

import scala.concurrent._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import Directives._
import blah.core._

class Service(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer,
  timeout: Timeout
) extends JsonProtocol with SprayJsonSupport {
  import system.dispatcher

  val route = pathPrefix("events") {
    (post & entity(as[EventApi.Create])) { req =>
      complete {
        Created -> (env.api ? req).mapTo[Event]
      }
    }
  }
}

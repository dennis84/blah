package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import blah.core.Message

class StatusService(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  def route =
    (get & path("")) {
      complete(Message("OK"))
    }
}

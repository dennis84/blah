package blah.admin

import akka.actor.ActorSystem
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import Directives._
import blah.core._

class Service(implicit system: ActorSystem)
  extends JsonProtocol
  with SprayJsonSupport {

  import system.dispatcher

  val route =
    (get & path("")) {
      complete("todo")
    }
}

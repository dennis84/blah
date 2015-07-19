package blah.api

import akka.actor._
import blah.core._

class Env(system: ActorSystem) {
  lazy val producer = Producer[String]("events_1")
  lazy val api = system.actorOf(Props(new EventApi(producer)))
}

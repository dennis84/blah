package blah.api

import akka.actor._
import blah.core._

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config
  lazy val producer = Producer[String]("events_2")
  lazy val api = system.actorOf(Props(new EventApi(producer)))
}

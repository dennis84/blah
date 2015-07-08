package blah.api

import akka.actor._
import blah.core._

class Env(system: ActorSystem) {
  lazy val producer = Producer[String]("foo")
  lazy val api = system.actorOf(Props(new Api(producer)))
}

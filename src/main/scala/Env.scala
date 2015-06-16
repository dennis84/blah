package blah

import akka.actor._

class Env(system: ActorSystem) {
  val api = system.actorOf(Props[Api])
}

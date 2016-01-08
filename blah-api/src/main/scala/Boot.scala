package blah.api

import akka.event.Logging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val log = Logging.getLogger(system, this)

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")
  val env = new Env(system)
  val service = new Service(env)

  Http().bindAndHandle(service.route, interface, port)
}

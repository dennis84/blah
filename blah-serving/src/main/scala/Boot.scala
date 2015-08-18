package blah.serving

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import blah.core.Consumer

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")
  val env = new Env(system)
  val services = Seq(
    new WebsocketService(env.websocket),
    new CountService(env),
    new SimilarityService(env))
  val routes = services.map(_.route)

  val consumer = Consumer("trainings")
  consumer.read() foreach (x => println(x))

  (for {
    head <- routes.headOption
    tail = routes.tail
  } yield tail.foldLeft(head) {
    case (xs, x) => xs ~ x
  }) map { route =>
    Http().bindAndHandle(route, interface, port)
  }
}

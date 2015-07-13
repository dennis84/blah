package blah.serving

import akka.actor._
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import blah.core.{ServingEnv, ServingWithRoute}

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val port = sys.env.get("PORT") map (_.toInt) getOrElse 9002

  val env = new ServingEnv(system)
  val websocketService = new WebsocketService(env.websocket)

  val services = Seq(new blah.example.Serving(env))
  val serviceRoutes = services
    .filter(_.isInstanceOf[ServingWithRoute])
    .asInstanceOf[Seq[ServingWithRoute]]
    .map(_.route)

  val route = (for {
    head <- serviceRoutes.headOption
    tail = serviceRoutes.tail
  } yield tail.foldLeft(head) {
    case (xs, x) => xs ~ x
  } ~ websocketService.route) getOrElse {
    websocketService.route
  }

  Http().bindAndHandle(route, "0.0.0.0", port)
}

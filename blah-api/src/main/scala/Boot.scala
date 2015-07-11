package blah.api

import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(5 seconds)

  val port = sys.env.get("PORT") map (_.toInt) getOrElse 9000
  val env = new Env(system)
  val service = new Service(env)

  Http().bindAndHandle(service.route, "0.0.0.0", port)
}

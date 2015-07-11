package blah.admin

import akka.actor._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val port = sys.env.get("PORT") map (_.toInt) getOrElse 9001
  val service = new Service

  Http().bindAndHandle(service.route, "0.0.0.0", port)
}

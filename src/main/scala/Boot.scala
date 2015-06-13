package blah

import akka.actor._
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http

object Boot extends App with Service {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val port = sys.env.get("PORT") map(_.toInt) getOrElse 9000

  Http().bindAndHandle(routes, "0.0.0.0", port)
}

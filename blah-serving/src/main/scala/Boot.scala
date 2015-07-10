package blah.serving

import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object Boot extends App with Service {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val timeout = Timeout(5 seconds)
  implicit val materializer = ActorMaterializer()

  val port = sys.env.get("PORT") map (_.toInt) getOrElse 9002

  Http().bindAndHandle(routes, "0.0.0.0", port)
}

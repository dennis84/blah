package blah.serving

import akka.actor._
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val port = sys.env.get("PORT") map (_.toInt) getOrElse 9002

  val env = new Env(system)
  val example = new blah.example.Serving(env.conn)

  Http().bindAndHandle(example.route, "0.0.0.0", port)
}

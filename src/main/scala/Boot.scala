package blah

import akka.actor._
import akka.stream._
import akka.http.Http

object Boot extends App with Service {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorFlowMaterializer()

  val port = sys.env.get("PORT") map(_.toInt) getOrElse 9000

  Http().bind("0.0.0.0", port).startHandlingWith(routes)
}

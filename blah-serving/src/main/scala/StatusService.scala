package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._

class StatusService(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service {
  import system.dispatcher

  def route =
    (get & path("")) {
      complete("OK")
    }
}

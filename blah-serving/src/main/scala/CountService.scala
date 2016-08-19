package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class CountService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with CountJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new CountRepo(env.elasticClient)

  def route =
    (post & path("count") & entity(as[CountQuery])) {
      case q@CountQuery(_, f, None) => complete(repo count q)
      case q@CountQuery(_, f, g)    => complete(repo search q)
    }
}

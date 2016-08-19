package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class MostViewedService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with MostViewedJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new MostViewedRepo(env.elasticClient)

  def route =
    (post & path("most-viewed") & entity(as[MostViewedQuery])) { q =>
      complete(repo find q)
    }
}

package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class SumService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new SumRepo(env.elasticClient)

  def route =
    (post & path("sum") & entity(as[SumQuery])) { q =>
      complete(repo sum q)
    }
}

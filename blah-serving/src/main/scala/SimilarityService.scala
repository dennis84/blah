package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class SimilarityService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with SimilarityJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new SimilarityRepo(env.elasticClient)

  def route =
    (post & path("similarity") & entity(as[SimilarityQuery])) {
      q => complete(repo find q)
    }
}

package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class RecommendationService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with RecommendationJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new RecommendationRepo(env.elasticClient)

  def route =
    (post & path("recommendation") & entity(as[RecommendationQuery])) {
      q => complete(repo find q)
    }
}

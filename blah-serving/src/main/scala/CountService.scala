package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class CountService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new CountRepo(env.elasticClient)

  def route =
    (post & path("count") & entity(as[Query])) {
      case q@Query(_, None) => complete(repo count q)
      case q@Query(_, _)    => complete(repo search q)
    }
}

package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class UserService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new UserRepo(env.elasticClient)

  def route =
    (post & path("user") & entity(as[UserQuery])) {
      case q@UserQuery(_, None) => complete(repo count q)
      case q@UserQuery(_, _)    => complete(repo search q)
    }
}

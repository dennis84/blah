package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.event.LoggingAdapter

class UserService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with UserJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new UserRepo(env.elasticClient)

  def route =
    (post & path("users") & entity(as[UserQuery])) {
      case q@UserQuery(_, None) => complete(repo search q)
    } ~
    (post & path("user-count") & entity(as[UserQuery])) {
      case q@UserQuery(_, None) => complete(repo count q)
      case q@UserQuery(_, _)    => complete(repo grouped q)
    }
}

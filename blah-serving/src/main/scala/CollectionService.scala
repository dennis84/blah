package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class CollectionService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with CollectionJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new CollectionRepo(env.elasticClient)

  def route =
    (post & path("collection") & entity(as[CollectionQuery])) { q =>
      complete(repo list q)
    }
}

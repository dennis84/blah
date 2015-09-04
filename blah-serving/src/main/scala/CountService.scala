package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class CountService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new CountRepo(env.cassandraConnection)

  def route =
    (post & path("count") & entity(as[Query])) { q =>
      complete(repo query q)
    } ~
    (get & path("count-all")) {
      parameterMap { params =>
        complete(repo countAll params.toJson.convertTo[CountAllQuery])
      }
    }
}

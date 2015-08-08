package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class SimilarityService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new SimilarityRepo(env.cassandraConnection)

  def route = (get & path("similarity")) {
    parameterMap { params =>
      complete(repo sims params.toJson.convertTo[SimilarityQuery])
    }
  }
}

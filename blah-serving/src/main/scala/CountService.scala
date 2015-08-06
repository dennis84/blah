package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import blah.core._

class CountService(env: Env)(
  implicit system: ActorSystem,
  materializer: Materializer
) extends Service with ServingJsonProtocol with SprayJsonSupport {
  import system.dispatcher

  private val repo = new CountRepo(env.cassandraConnection)

  def route = (get & path("count")) {
    parameterMap { params =>
      complete(repo count params.toJson.convertTo[CountQuery])
    }
  }
}

package blah.example

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import blah.core.{ServingEnv, Serving}

class CountServing(env: ServingEnv)
  extends Serving
  with CountJsonProtocol
  with SprayJsonSupport {
  import env.system.dispatcher

  def id = "count"

  private val repo = new Repo(env.cassandraConnection)

  def serve(q: Map[String, String]) =
    repo.count(q.toJson.convertTo[CountQuery])
}

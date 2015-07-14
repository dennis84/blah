package blah.example

import scala.concurrent.duration._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import blah.core.{ServingEnv, Serving}

class ExampleServing(env: ServingEnv)
  extends Serving
  with ExampleJsonProtocol
  with SprayJsonSupport {
  import env.system.dispatcher

  def id = "example"

  private val repo = new Repo(env.cassandraConnection)

  override def serve = complete(repo.findAll)

  env.system.scheduler.schedule(1.second, 1.second) {
    repo.findAll map (xs => env.websocket.send("example", xs.toJson.compactPrint))
  }
}

package blah.example

import scala.concurrent.duration._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import spray.json._
import blah.core.{ServingEnv, Serving}

class ExampleServing(env: ServingEnv)
  extends Serving
  with ExampleJsonProtocol {
  import env.system.dispatcher

  def id = "example"

  private val repo = new Repo(env.cassandraConnection)

  env.system.scheduler.schedule(1.second, 1.second) {
    repo.findAll map (xs => env.websocket.send("example", xs.toJson.compactPrint))
  }
}

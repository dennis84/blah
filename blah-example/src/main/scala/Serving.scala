package blah.example

import scala.concurrent.duration._
import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.scaladsl.{Sink, Source, Flow}
import spray.json._
import com.datastax.driver.core.Session
import blah.core.{ServingEnv, ServingWithRoute}

class Serving(env: ServingEnv)(implicit system: ActorSystem)
  extends ExampleJsonProtocol {
  import system.dispatcher

  val repo = new Repo(env.cassandraConnection)

  system.scheduler.schedule(1.second, 1.second) {
    repo.findAll map (xs => env.websocket.send(xs.toJson.compactPrint))
  }
}

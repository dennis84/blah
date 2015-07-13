package blah.example

import scala.concurrent.duration._
import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.scaladsl.{Sink, Source, Flow}
import spray.json._
import com.datastax.driver.core.Session
import blah.core.WebsocketHub

class Serving(conn: Session, hub: WebsocketHub)(implicit system: ActorSystem)
  extends ExampleJsonProtocol {

  import system.dispatcher
  val repo = new Repo(conn)

  system.scheduler.schedule(1.second, 1.second) {
    repo.findAll map (xs => hub.send(xs.toJson.compactPrint))
  }
}

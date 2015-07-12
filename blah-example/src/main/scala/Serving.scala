package blah.example

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.scaladsl.{Sink, Source, Flow}
import Directives._
import spray.json._
import com.datastax.driver.core.Session

class Serving(conn: Session)(implicit system: ActorSystem)
  extends ExampleJsonProtocol
  with SprayJsonSupport {

  import system.dispatcher
  val repo = new Repo(conn)

  val route =
    pathPrefix("example") {
      (get & path("")) {
        complete {
          OK -> repo.findAll
        }
      } ~
      (get & path("ws")) {
        handleWebsocketMessages(flow)
      }
    }

  def flow = Flow.wrap(Sink.ignore, source.map(TextMessage.Strict))((_, _) => ())

  def source = Source(1.second, 1.second, "tick").mapAsync(1) {
    _ => repo.findAll map (_.toJson.compactPrint)
  }
}

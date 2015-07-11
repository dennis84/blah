package blah.example

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes._
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
      }
    }
}

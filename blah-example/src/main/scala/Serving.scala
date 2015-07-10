package blah.example

import scala.concurrent.ExecutionContextExecutor
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import spray.json._
import com.datastax.driver.core.Session

trait Serving extends ExampleJsonProtocol with SprayJsonSupport {
  implicit val executor: ExecutionContextExecutor
  val conn: Session
  val repo = new Repo(conn)

  val exampleRoutes =
    pathPrefix("example") {
      (get & path("")) {
        complete {
          OK -> repo.findAll
        }
      }
    }
}

package blah.admin

import scala.concurrent._
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.FlowMaterializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import blah.core._

trait Service extends Protocols with SprayJsonSupport {
  implicit val timeout: Timeout
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: FlowMaterializer

  val env: Env

  val routes =
    (get & path("")) {
      complete {
        OK -> (env.api ? FindAll).mapTo[List[Event]]
      }
    }
}

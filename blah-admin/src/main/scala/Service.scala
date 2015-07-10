package blah.admin

import scala.concurrent._
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import blah.core._

trait Service extends JsonProtocol with SprayJsonSupport {
  implicit val timeout: Timeout
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: ActorMaterializer

  val env: Env

  val routes =
    (get & path("")) {
      complete("todo")
    }
}

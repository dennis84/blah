package blah

import scala.concurrent._
import akka.actor._
import akka.stream._
import akka.http.marshallers.sprayjson.SprayJsonSupport._
import akka.http.server._
import Directives._

trait Service {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: FlowMaterializer

  val routes =
    get(complete("Hello World"))
}

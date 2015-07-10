package blah.serving

import scala.concurrent._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import Directives._
import blah.core._

trait Service extends blah.example.Serving {
  implicit val system: ActorSystem
  implicit val timeout: Timeout
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: ActorMaterializer

  lazy val env = new Env(system)
  lazy val conn = env.conn

  val routes = exampleRoutes
}

package blah

import scala.concurrent._
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server._
import Directives._

trait Service extends Protocols {
  implicit val timeout: Timeout
  implicit val executor: ExecutionContextExecutor

  val env: Env

  val routes =
    path("") {
      get(complete {
        (env.api ? ArticleReq("Hello World", "...")).mapTo[Article]
      })
    }
}

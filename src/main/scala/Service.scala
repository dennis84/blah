package blah

import scala.concurrent._
import akka.pattern.ask
import akka.util.Timeout
import akka.stream.FlowMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import Directives._

trait Service extends Protocols with SprayJsonSupport {
  implicit val timeout: Timeout
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: FlowMaterializer

  val env: Env

  val routes =
    pathPrefix("articles") {
      (post & entity(as[CreateArticle])) { req =>
        complete {
          Created -> (env.api ? req).mapTo[Article]
        }
      } ~
      (get & path(Segment)) { id =>
        complete {
          OK -> (env.api ? GetArticle(id)).mapTo[Article]
        }
      }
    }
}

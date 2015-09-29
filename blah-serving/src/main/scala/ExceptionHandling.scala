package blah.serving

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._

trait ExceptionHandling {
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _: Exception => complete(
      HttpResponse(InternalServerError, entity = "Oops!"))
  }
}

object ExceptionHandling extends ExceptionHandling

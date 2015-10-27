package blah.serving

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait ExceptionHandling extends SprayJsonSupport with ServingJsonProtocol {
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _: Exception => complete(InternalServerError -> Status("Internal Server Error"))
  }
}

object ExceptionHandling extends ExceptionHandling

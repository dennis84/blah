package blah.serving

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import StatusCodes._
import Directives._

trait ExceptionHandling extends SprayJsonSupport with ServingJsonProtocol {
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _: Exception => complete(InternalServerError -> Status("Internal Server Error"))
  }
}

object ExceptionHandling extends ExceptionHandling

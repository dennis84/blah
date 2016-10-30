package blah.serving

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

trait ExceptionHandling extends SprayJsonSupport with ServingJsonProtocol {
  implicit def exceptionHandler(implicit log: LoggingAdapter) = ExceptionHandler {
    case e: Exception =>
      log.error(e, e.getMessage)
      complete(InternalServerError -> Message("Internal Server Error"))
  }
}

object ExceptionHandling extends ExceptionHandling

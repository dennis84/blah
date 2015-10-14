package blah.serving

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import StatusCodes._
import Directives._

trait RejectionHandling extends SprayJsonSupport with ServingJsonProtocol {
  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(NotFound -> Status("Not Found")) }
    .result()
}

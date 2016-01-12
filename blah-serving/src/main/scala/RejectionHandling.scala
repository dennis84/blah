package blah.serving

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import blah.core.Message

trait RejectionHandling extends SprayJsonSupport with ServingJsonProtocol {
  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(NotFound -> Message("Not Found")) }
    .result()
}

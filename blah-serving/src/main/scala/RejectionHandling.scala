package blah.serving

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import blah.core.Message
import blah.core.CorsSupport

trait RejectionHandling
  extends SprayJsonSupport
  with ServingJsonProtocol
  with CorsSupport {

  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handleNotFound { complete(NotFound -> Message("Not Found")) }
    .handle { case MalformedRequestContentRejection(msg, _) =>
      withAccessControlHeaders(complete {
        BadRequest -> "The request content was malformed:\n" + msg
      })
    }
    .result()
}

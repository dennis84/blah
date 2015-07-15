package blah.core

import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import Directives._

trait Serving {
  def id: String

  final def route = pathPrefix(id)(get(parameterMap { params =>
    complete(serve(params))
  }))

  def serve(q: Map[String, String]): ToResponseMarshallable
}

package blah.core

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import Directives._

trait Serving {
  def id: String
  def serve: Route = complete(NotImplemented -> "Not implemented!")
  final def route = pathPrefix(id)(get(serve))
}

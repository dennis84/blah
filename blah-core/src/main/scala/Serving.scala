package blah.core

import akka.http.scaladsl.server.Route

trait ServingWithRoute {
  def route: Route
}

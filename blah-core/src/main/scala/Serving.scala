package blah.core

import akka.http.scaladsl.server.Route

trait Serving {
  def route: Route
}

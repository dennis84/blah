package blah.serving

import akka.http.scaladsl.server.Route

trait Service {
  def route: Route
}

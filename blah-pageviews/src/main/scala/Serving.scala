package blah.pageviews

import akka.http.scaladsl.server._
import Directives._

object Serving {

  val routes =
    pathPrefix("pageviews") {
      (get & path("")) {
        complete("pageviews")
      }
    }
}

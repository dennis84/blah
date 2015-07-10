package blah.example

import akka.http.scaladsl.server._
import Directives._

object Serving {

  val routes =
    pathPrefix("example") {
      (get & path("")) {
        complete("example")
      }
    }
}

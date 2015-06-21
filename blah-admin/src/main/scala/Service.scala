package blah.admin

import akka.http.scaladsl.server._
import Directives._

trait Service {

  val routes =
    (get & path("")) {
      complete("Home")
    }
}

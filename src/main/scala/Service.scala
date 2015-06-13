package blah

import akka.http.scaladsl.server._
import Directives._

trait Service {

  val routes =
    get(complete("Hello World"))
}

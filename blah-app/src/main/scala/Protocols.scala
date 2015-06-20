package blah.app

import spray.json._
import blah.core._

trait Protocols extends DefaultJsonProtocol {
  implicit val eventReq = jsonFormat2(EventReq)
  implicit val event = jsonFormat3(Event)
}

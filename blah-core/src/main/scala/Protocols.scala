package blah.core

import spray.json._

trait Protocols extends DefaultJsonProtocol {
  implicit val eventReq = jsonFormat2(EventReq)
  implicit val event = jsonFormat3(Event)
}

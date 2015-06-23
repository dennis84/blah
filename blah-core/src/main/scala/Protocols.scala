package blah.core

import spray.json._

trait Protocols extends DefaultJsonProtocol {
  implicit val createEvent = jsonFormat2(CreateEvent)
  implicit val event = jsonFormat3(Event)
}

package blah.core

import spray.json._

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val createEvent = jsonFormat2(CreateEvent)
  implicit val event = jsonFormat3(Event)
}

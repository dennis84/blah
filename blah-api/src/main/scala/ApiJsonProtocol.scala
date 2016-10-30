package blah.api

import spray.json._
import blah.json.TimeJsonProtocol

object ApiJsonProtocol extends TimeJsonProtocol {
  implicit val eventFmt = jsonFormat4(Event)
  implicit val messageFmt = jsonFormat1(Message)
}

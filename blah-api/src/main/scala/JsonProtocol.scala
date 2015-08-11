package blah.api

import spray.json._
import blah.core.JsonProtocol

trait ApiJsonProtocol extends JsonProtocol {
  implicit val createEventFmt = jsonFormat2(EventApi.Create)
  implicit val messageFmt = jsonFormat1(EventApi.Message)
}

object ApiJsonProtocol extends ApiJsonProtocol

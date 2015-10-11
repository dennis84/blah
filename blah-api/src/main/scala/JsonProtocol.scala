package blah.api

import blah.core.JsonProtocol

trait ApiJsonProtocol extends JsonProtocol {
  implicit val createEventFmt = jsonFormat2(Service.Create)
  implicit val messageFmt = jsonFormat1(Service.Message)
}

object ApiJsonProtocol extends ApiJsonProtocol

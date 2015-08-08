package blah.api

import spray.json._
import blah.core.JsonProtocol

trait ApiJsonProtocol extends JsonProtocol {
  implicit val createEventFmt = jsonFormat2(EventApi.Create)
}

object ApiJsonProtocol extends ApiJsonProtocol

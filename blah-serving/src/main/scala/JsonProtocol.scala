package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val filterFmt = jsonFormat3(Filter)
}

object ServingJsonProtocol extends ServingJsonProtocol

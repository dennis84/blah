package blah.serving

import blah.json.TimeJsonProtocol

trait ServingJsonProtocol extends TimeJsonProtocol {
  implicit val filterFmt = jsonFormat3(Filter)
  implicit val messageFmt = jsonFormat1(Message)
}

object ServingJsonProtocol extends ServingJsonProtocol

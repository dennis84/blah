package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val countFmt = jsonFormat1(CountResult)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
}

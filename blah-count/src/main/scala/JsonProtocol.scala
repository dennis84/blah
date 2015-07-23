package blah.count

import blah.core.JsonProtocol

trait CountJsonProtocol extends JsonProtocol {
  implicit val countFmt = jsonFormat1(CountResult)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val countPropsFmt = jsonFormat1(CountProps)
  implicit val countEventFmt = jsonFormat4(CountEvent)
}

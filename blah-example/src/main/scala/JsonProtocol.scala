package blah.example

import spray.json._
import blah.core.JsonProtocol

trait CountJsonProtocol extends JsonProtocol {
  implicit val countFmt = jsonFormat1(CountResult)
}

package blah.example

import spray.json._
import blah.core.JsonProtocol

trait ExampleJsonProtocol extends JsonProtocol {
  implicit val example = jsonFormat3(Example)
}

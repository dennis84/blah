package blah.example

import spray.json._

trait ExampleJsonProtocol extends DefaultJsonProtocol {
  implicit val example = jsonFormat2(Example)
}

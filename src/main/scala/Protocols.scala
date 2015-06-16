package blah

import spray.json._

trait Protocols extends DefaultJsonProtocol {
  implicit val article = jsonFormat2(Article)
}

package blah.app

import spray.json._
import blah.core._

trait Protocols extends DefaultJsonProtocol {
  implicit val createArticle = jsonFormat2(CreateArticle)

  implicit val article = jsonFormat3(Article)
}

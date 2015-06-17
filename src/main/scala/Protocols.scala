package blah

import spray.json._

trait Protocols extends DefaultJsonProtocol {
  implicit val createArticle = jsonFormat2(CreateArticle)

  implicit val article = jsonFormat3(Article)
}

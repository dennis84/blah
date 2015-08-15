package blah.serving

import spray.json._
import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val countFmt = jsonFormat1(CountResult)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val similarityFmt = jsonFormat2(SimilarityResult)
  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
}

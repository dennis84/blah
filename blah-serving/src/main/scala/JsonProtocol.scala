package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val countQueryFmt = jsonFormat2(CountQuery)
  implicit val countResultFmt = jsonFormat1(CountResult)

  implicit val similarityFmt = jsonFormat2(SimilarityResult)
  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
}

object ServingJsonProtocol extends ServingJsonProtocol

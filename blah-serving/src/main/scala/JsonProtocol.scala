package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val countQueryFmt = jsonFormat2(CountQuery)
  implicit val countResultFmt = jsonFormat1(CountResult)

  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
  implicit val similarityItemFmt = jsonFormat2(SimilarityItem)
  implicit val similarityResultFmt = jsonFormat2(SimilarityResult)
}

object ServingJsonProtocol extends ServingJsonProtocol

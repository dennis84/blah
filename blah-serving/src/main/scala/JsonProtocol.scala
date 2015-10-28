package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val countQueryFmt = jsonFormat2(CountQuery)
  implicit val countFmt = jsonFormat7(Count)

  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
  implicit val similarityItemFmt = jsonFormat2(SimilarityItem)
  implicit val similarityResultFmt = jsonFormat2(SimilarityResult)

  implicit val userQueryFmt = jsonFormat2(UserQuery)
  implicit val userResultFmt = jsonFormat2(UserCount)

  implicit val statusFmt = jsonFormat1(Status)
}

object ServingJsonProtocol extends ServingJsonProtocol

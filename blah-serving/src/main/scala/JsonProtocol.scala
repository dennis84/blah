package blah.serving

import spray.json._
import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val queryFmt = jsonFormat2(Query)
  implicit val countResultFmt = jsonFormat1(CountResult)
  implicit val groupedCountResultFmt = jsonFormat1(GroupedCountResult)

  implicit val similarityFmt = jsonFormat2(SimilarityResult)
  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
}

object ServingJsonProtocol extends ServingJsonProtocol

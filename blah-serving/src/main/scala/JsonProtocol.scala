package blah.serving

import spray.json._
import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val queryFmt = jsonFormat2(Query)
  implicit val pageViewFmt = jsonFormat2(PageView)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val countResultFmt = jsonFormat1(CountResult)
  implicit val countAllQueryFmt = jsonFormat2(CountAllQuery)
  implicit val countAllResultFmt = jsonFormat1(CountAllResult)
  implicit val similarityFmt = jsonFormat2(SimilarityResult)
  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
}

object ServingJsonProtocol extends ServingJsonProtocol

package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val filterFmt = jsonFormat3(Filter)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val userQueryFmt = jsonFormat2(UserQuery)
  implicit val countFmt = jsonFormat7(Count)
  implicit val similarityQueryFmt = jsonFormat2(SimilarityQuery)
  implicit val similarityItemFmt = jsonFormat2(SimilarityItem)
  implicit val similarityResultFmt = jsonFormat2(SimilarityResult)
  implicit val userEventRespFmt = jsonFormat4(UserEvent)
  implicit val userFmt = jsonFormat11(User)
  implicit val userCountFmt = jsonFormat2(UserCount)
  implicit val sumQueryFmt = jsonFormat3(SumQuery)
  implicit val sumFmt = jsonFormat1(Sum)
  implicit val funnelQueryFmt = jsonFormat1(FunnelQuery)
  implicit val funnelFmt = jsonFormat3(Funnel)
}

object ServingJsonProtocol extends ServingJsonProtocol

package blah.serving

import blah.core.JsonProtocol

trait ServingJsonProtocol extends JsonProtocol {
  implicit val filterFmt = jsonFormat3(Filter)
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val userQueryFmt = jsonFormat2(UserQuery)
  implicit val countFmt = jsonFormat7(Count)
  implicit val recommendationQueryFmt = jsonFormat3(RecommendationQuery)
  implicit val recommendationItemFmt = jsonFormat2(RecommendationItem)
  implicit val similarityQueryFmt = jsonFormat3(SimilarityQuery)
  implicit val similarityItemFmt = jsonFormat2(SimilarityItem)
  implicit val userEventFmt = jsonFormat5(UserEvent)
  implicit val userFmt = jsonFormat13(User)
  implicit val userCountFmt = jsonFormat2(UserCount)
  implicit val sumQueryFmt = jsonFormat3(SumQuery)
  implicit val sumFmt = jsonFormat1(Sum)
  implicit val funnelQueryFmt = jsonFormat1(FunnelQuery)
  implicit val funnelFmt = jsonFormat4(Funnel)
  implicit val mostViewedQueryFmt = jsonFormat2(MostViewedQuery)
  implicit val mostViewedFmt = jsonFormat2(MostViewed)
  implicit val referrerQueryFmt = jsonFormat1(ReferrerQuery)
  implicit val referrerFmt = jsonFormat2(Referrer)
}

object ServingJsonProtocol extends ServingJsonProtocol

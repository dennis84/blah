package blah.serving

trait RecommendationJsonFormat extends ServingJsonProtocol {
  implicit val recommendationQueryFmt = jsonFormat3(RecommendationQuery)
  implicit val recommendationItemFmt = jsonFormat2(RecommendationItem)
}

package blah.serving

trait MostViewedJsonFormat extends ServingJsonProtocol {
  implicit val mostViewedQueryFmt = jsonFormat2(MostViewedQuery)
  implicit val mostViewedFmt = jsonFormat2(MostViewed)
}

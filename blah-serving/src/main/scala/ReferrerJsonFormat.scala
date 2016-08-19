package blah.serving

trait ReferrerJsonFormat extends ServingJsonProtocol {
  implicit val referrerQueryFmt = jsonFormat1(ReferrerQuery)
  implicit val referrerFmt = jsonFormat2(Referrer)
}

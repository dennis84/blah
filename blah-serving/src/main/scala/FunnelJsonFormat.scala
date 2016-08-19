package blah.serving

trait FunnelJsonFormat extends ServingJsonProtocol {
  implicit val funnelQueryFmt = jsonFormat1(FunnelQuery)
  implicit val funnelFmt = jsonFormat4(Funnel)
}

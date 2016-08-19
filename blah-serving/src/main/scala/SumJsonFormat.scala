package blah.serving

trait SumJsonFormat extends ServingJsonProtocol {
  implicit val sumQueryFmt = jsonFormat3(SumQuery)
  implicit val sumFmt = jsonFormat1(Sum)
}

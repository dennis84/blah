package blah.serving

trait CountJsonFormat extends ServingJsonProtocol {
  implicit val countQueryFmt = jsonFormat3(CountQuery)
  implicit val countFmt = jsonFormat7(Count)
}

package blah.serving

trait CollectionJsonFormat extends ServingJsonProtocol {
  implicit val collectionQueryFmt = jsonFormat1(CollectionQuery)
  implicit val collectionFmt = jsonFormat3(Collection)
}

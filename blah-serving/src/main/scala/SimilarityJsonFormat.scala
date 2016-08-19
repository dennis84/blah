package blah.serving

trait SimilarityJsonFormat extends ServingJsonProtocol {
  implicit val similarityQueryFmt = jsonFormat3(SimilarityQuery)
  implicit val similarityItemFmt = jsonFormat2(SimilarityItem)
}

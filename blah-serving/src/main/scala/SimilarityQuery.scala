package blah.serving

case class SimilarityQuery(
  items: List[String] = Nil,
  collection: Option[String] = None,
  limit: Option[Int] = None)

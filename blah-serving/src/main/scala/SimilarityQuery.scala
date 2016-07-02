package blah.serving

case class SimilarityQuery(
  items: List[String] = Nil,
  limit: Option[Int] = None)

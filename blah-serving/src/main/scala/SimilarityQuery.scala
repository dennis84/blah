package blah.serving

case class SimilarityQuery(
  user: String,
  limit: Option[String] = None)

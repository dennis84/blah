package blah.serving

case class SimilarityResult(
  user: String,
  views: List[String] = Nil)

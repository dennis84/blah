package blah.serving

case class SimilarityResult(
  user: String,
  views: List[SimilarityItem] = Nil)

case class SimilarityItem(
  page: String,
  score: Double)

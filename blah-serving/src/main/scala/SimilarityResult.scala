package blah.serving

case class SimilarityResult(
  user: String,
  views: List[SimilarityItem] = Nil)

case class SimilarityItem(
  item: String,
  score: Double)

package blah.serving

case class RecommendationResult(
  user: String,
  views: List[RecommendationItem] = Nil)

case class RecommendationItem(
  item: String,
  score: Double)

package blah.serving

case class RecommendationResult(
  user: String,
  collection: Option[String],
  items: List[RecommendationItem] = Nil)

case class RecommendationItem(
  item: String,
  score: Double)

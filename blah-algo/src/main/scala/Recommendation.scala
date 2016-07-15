package blah.algo

case class Recommendation(
  user: String,
  collection: Option[String],
  items: List[RecommendationItem] = Nil)

case class RecommendationItem(
  item: String,
  score: Double)

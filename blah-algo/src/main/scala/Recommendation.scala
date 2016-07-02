package blah.algo

case class Recommendation(
  user: String,
  views: List[RecommendationItem] = Nil)

case class RecommendationItem(
  item: String,
  score: Double)

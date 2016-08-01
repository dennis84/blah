package blah.algo

case class Recommendation(
  id: String,
  user: String,
  collection: Option[String] = None,
  items: Seq[RecommendationItem] = Nil)

case class RecommendationItem(
  item: String,
  score: Double)

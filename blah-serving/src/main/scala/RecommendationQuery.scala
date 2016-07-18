package blah.serving

case class RecommendationQuery(
  user: String,
  collection: Option[String] = None,
  limit: Option[Int] = None)

package blah.serving

case class RecommendationQuery(
  user: String,
  limit: Option[String] = None)

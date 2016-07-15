package blah.serving

case class RecommendationQuery(
  user: String,
  collection: Option[String] = None,
  limit: Option[String] = None)

package blah.serving

case class MostViewedQuery(
  collection: String,
  limit: Option[String] = None)

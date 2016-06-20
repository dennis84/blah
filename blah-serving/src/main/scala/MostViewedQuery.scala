package blah.serving

case class MostViewedQuery(
  collection: String,
  limit: Int = 100)

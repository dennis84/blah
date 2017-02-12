package blah.recommendation

import org.apache.spark.sql.Row

case class RecommendationEvent(
  collection: String,
  user: String,
  item: String)

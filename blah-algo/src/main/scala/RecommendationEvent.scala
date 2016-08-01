package blah.algo

import org.apache.spark.sql.Row

case class RecommendationEvent(
  collection: String,
  user: String,
  item: String)

package blah.algo

import org.apache.spark.sql.Row

case class SimilarityEvent(
  collection: String,
  user: String,
  item: String)

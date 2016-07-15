package blah.algo

import org.apache.spark.sql.Row

case class RecommendationEvent(
  collection: String,
  user: Option[String] = None,
  item: Option[String] = None)

object RecommendationEvent {
  def apply(r: Row): RecommendationEvent = RecommendationEvent(
    r.getString(0),
    Option(r.getString(1)),
    Option(r.getString(2)))
}

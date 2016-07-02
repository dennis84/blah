package blah.algo

import org.apache.spark.sql.Row

case class RecommendationEvent(
  user: Option[String] = None,
  item: Option[String] = None)

object RecommendationEvent {
  def apply(r: Row): RecommendationEvent = RecommendationEvent(
    Option(r.getString(0)),
    Option(r.getString(1)))
}

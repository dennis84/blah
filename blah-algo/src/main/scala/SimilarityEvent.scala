package blah.algo

import org.apache.spark.sql.Row

case class SimilarityEvent(
  collection: String,
  user: Option[String] = None,
  item: Option[String] = None)

object SimilarityEvent {
  def apply(r: Row): SimilarityEvent = SimilarityEvent(
    r.getString(0),
    Option(r.getString(1)),
    Option(r.getString(2)))
}

package blah.algo

import org.apache.spark.sql.Row

case class MostViewedEvent(
  date: String,
  collection: String,
  item: Option[String] = None)

object MostViewedEvent {
  def apply(r: Row): MostViewedEvent = MostViewedEvent(
    r.getString(0),
    r.getString(1),
    Option(r.getString(2)))
}

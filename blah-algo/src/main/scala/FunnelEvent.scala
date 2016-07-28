package blah.algo

import org.apache.spark.sql.Row

case class FunnelEvent(
  date: String,
  user: Option[String] = None,
  item: Option[String] = None)

object FunnelEvent {
  def apply(r: Row): FunnelEvent = FunnelEvent(
    r.getString(0),
    Option(r.getString(1)),
    Option(r.getString(2)))
}

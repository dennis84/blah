package blah.algo

import java.time.ZonedDateTime
import org.apache.spark.sql.Row

case class FunnelEvent(
  date: ZonedDateTime,
  user: Option[String] = None,
  item: Option[String] = None)

object FunnelEvent {
  def apply(r: Row): FunnelEvent = FunnelEvent(
    ZonedDateTime.parse(r.getString(0)),
    Option(r.getString(1)),
    Option(r.getString(2)))
}

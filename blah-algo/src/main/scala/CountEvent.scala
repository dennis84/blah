package blah.algo

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import org.apache.spark.sql.Row

case class CountEvent(
  date: String,
  collection: String,
  item: Option[String] = None,
  userAgent: Option[String] = None)

object CountEvent {
  def apply(r: Row): CountEvent = CountEvent(
    date = ZonedDateTime.parse(r.getString(0)).truncatedTo(ChronoUnit.HOURS).toString,
    collection = r.getString(1),
    item = Option(r.getString(2)),
    userAgent = Option(r.getString(3)))
}

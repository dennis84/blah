package blah.algo

import java.time.ZonedDateTime
import org.apache.spark.sql.Row

case class CountEvent(
  date: ZonedDateTime,
  collection: String,
  item: Option[String] = None,
  userAgent: Option[String] = None,
  price: Option[Double] = None)

object CountEvent {
  def apply(r: Row): CountEvent = CountEvent(
    date = ZonedDateTime.parse(r.getString(0)),
    collection = r.getString(1),
    item = Option(r.getString(2)),
    userAgent = Option(r.getString(3)),
    price = Option(r.getAs[Double](4)))
}

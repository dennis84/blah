package blah.algo

import org.apache.spark.sql.Row

case class CountEvent(
  date: String,
  collection: String,
  item: Option[String] = None,
  userAgent: Option[String] = None,
  price: Option[Double] = None)

object CountEvent {
  def apply(r: Row): CountEvent = CountEvent(
    date = r.getString(0),
    collection = r.getString(1),
    item = Option(r.getString(2)),
    userAgent = Option(r.getString(3)),
    price = Option(r.getAs[Double](4)))
}

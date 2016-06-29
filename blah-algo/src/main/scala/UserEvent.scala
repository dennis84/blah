package blah.algo

import org.apache.spark.sql.Row

case class UserEvent(
  date: String,
  collection: String,
  user: Option[String] = None,
  email: Option[String] = None,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

object UserEvent {
  def apply(r: Row): UserEvent = UserEvent(
    r.getString(0),
    r.getString(1),
    Option(r.getString(2)),
    Option(r.getString(3)),
    Option(r.getString(4)),
    Option(r.getString(5)),
    Option(r.getString(6)),
    Option(r.getString(7)),
    Option(r.getString(8)))
}

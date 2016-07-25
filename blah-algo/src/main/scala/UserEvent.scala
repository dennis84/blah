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
    date = r.getString(0),
    collection = r.getString(1),
    user = Option(r.getString(2)),
    email = Option(r.getString(3)),
    firstname = Option(r.getString(4)),
    lastname = Option(r.getString(5)),
    item = Option(r.getString(6)),
    title = Option(r.getString(7)),
    ip = Option(r.getString(8)))
}

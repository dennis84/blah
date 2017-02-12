package blah.user

import org.apache.spark.sql.Row

case class UserEvent(
  date: String,
  collection: String,
  user: String,
  email: Option[String] = None,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

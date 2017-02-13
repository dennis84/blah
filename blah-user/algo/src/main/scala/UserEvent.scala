package blah.user

import java.sql.Timestamp
import org.apache.spark.sql.Row

case class UserEvent(
  date: Timestamp,
  collection: String,
  user: String,
  email: Option[String] = None,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

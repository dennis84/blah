package blah.serving

import java.time.ZonedDateTime

case class UserEvent(
  date: ZonedDateTime,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

case class User(
  user: String,
  events: List[UserEvent] = Nil)

package blah.serving

import java.time.ZonedDateTime

case class UserEvent(
  date: ZonedDateTime,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

case class User(
  user: String,
  date: ZonedDateTime,
  lng: Double,
  lat: Double,
  country: String,
  countryCode: String,
  city: String,
  zipCode: String,
  events: List[UserEvent] = Nil)

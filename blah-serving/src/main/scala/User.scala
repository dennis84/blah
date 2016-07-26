package blah.serving

import java.time.ZonedDateTime

case class UserEvent(
  date: ZonedDateTime,
  collection: String,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

case class User(
  user: String,
  date: ZonedDateTime,
  email: Option[String] = None,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  lng: Option[Double] = None,
  lat: Option[Double] = None,
  country: Option[String] = None,
  countryCode: Option[String] = None,
  city: Option[String] = None,
  zipCode: Option[String] = None,
  events: List[UserEvent] = Nil,
  nbEvents: Int = 0)

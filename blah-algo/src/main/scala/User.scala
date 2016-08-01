package blah.algo

case class User(
  id: String,
  user: String,
  date: String,
  email: Option[String] = None,
  firstname: Option[String] = None,
  lastname: Option[String] = None,
  lng: Option[Double] = None,
  lat: Option[Double] = None,
  country: Option[String] = None,
  countryCode: Option[String] = None,
  city: Option[String] = None,
  zipCode: Option[String] = None,
  events: Seq[UserEvent] = Nil,
  nbEvents: Long = 0)

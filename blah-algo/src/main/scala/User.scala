package blah.algo

case class User(
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
  events: List[UserEvent] = Nil,
  nbEvents: Int = 0)

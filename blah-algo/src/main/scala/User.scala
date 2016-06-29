package blah.algo

case class User(
  user: String,
  email: String,
  firstname: String,
  lastname: String,
  date: String,
  lng: Double,
  lat: Double,
  country: String,
  countryCode: String,
  city: String,
  zipCode: String,
  events: List[UserEvent],
  nbEvents: Int)

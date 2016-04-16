package blah.algo

case class User(
  user: String,
  date: String,
  lng: Double,
  lat: Double,
  country: String,
  countryCode: String,
  city: String,
  zipCode: String,
  events: List[UserEvent])

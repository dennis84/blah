package blah.referrer

case class Referrer(
  id: String,
  collection: String,
  url: String,
  count: Long = 0)

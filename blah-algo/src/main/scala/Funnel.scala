package blah.algo

case class Funnel(
  name: String,
  item: String,
  next: Option[String] = None,
  count: Long = 0)

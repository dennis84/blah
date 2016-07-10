package blah.algo

case class Funnel(
  name: String,
  item: String,
  parent: Option[String] = None,
  count: Long = 0)

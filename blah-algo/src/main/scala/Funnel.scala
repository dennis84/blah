package blah.algo

case class Funnel(
  id: String,
  name: String,
  item: String,
  parent: Option[String] = None,
  count: Long = 0)

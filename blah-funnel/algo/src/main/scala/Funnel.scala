package blah.funnel

case class Funnel(
  id: String,
  name: String,
  item: String,
  parent: Option[String] = None,
  count: Long = 0)

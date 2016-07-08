package blah.serving

case class Funnel(
  name: String,
  item: String,
  next: Option[String] = None,
  count: Long = 0)

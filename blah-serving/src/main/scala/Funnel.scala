package blah.serving

case class Funnel(
  name: String,
  path: List[String] = Nil,
  count: Long = 0)

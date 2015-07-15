package blah.example

case class CountQuery(
  val event: String,
  val timeframe: Option[String] = None)

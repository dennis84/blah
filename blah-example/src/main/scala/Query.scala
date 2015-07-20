package blah.example

case class CountQuery(
  val event: String,
  val from: Option[String] = None,
  val to: Option[String] = None,
  val timeframe: Option[String] = None)

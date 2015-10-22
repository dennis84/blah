package blah.serving

case class UserCount(
  count: Long,
  country: Option[String] = None)

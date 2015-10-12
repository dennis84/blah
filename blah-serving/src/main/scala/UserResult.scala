package blah.serving

case class UserResult(
  user: String,
  ip: Option[String] = None)

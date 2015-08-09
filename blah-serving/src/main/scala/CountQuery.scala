package blah.serving

import org.joda.time.DateTime

case class CountQuery(
  event: Option[String] = None,
  from: Option[DateTime] = None,
  to: Option[DateTime] = None)

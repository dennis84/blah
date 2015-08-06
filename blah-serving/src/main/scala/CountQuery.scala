package blah.serving

import org.joda.time.DateTime

case class CountQuery(
  event: String,
  from: Option[DateTime] = None,
  to: Option[DateTime] = None)

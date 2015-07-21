package blah.count

import org.joda.time.DateTime

case class CountQuery(
  val event: String,
  val from: Option[DateTime] = None,
  val to: Option[DateTime] = None)

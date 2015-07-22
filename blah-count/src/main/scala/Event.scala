package blah.count

import org.joda.time.DateTime

case class CountProps(
  val event: String)

case class CountEvent(
  val id: String,
  val name: String,
  val date: DateTime,
  val props: CountProps)

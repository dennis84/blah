package blah.algo

import org.joda.time.DateTime

case class Count(
  name: String,
  date: DateTime,
  browserFamily: Option[String] = None,
  browserMajor: Option[String] = None,
  browserMinor: Option[String] = None,
  osFamily: Option[String] = None,
  osMajor: Option[String] = None,
  osMinor: Option[String] = None,
  deviceFamily: Option[String] = None,
  count: Option[Long] = None
) extends Serializable

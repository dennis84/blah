package blah.serving

import java.time.ZonedDateTime

case class Count(
  count: Long,
  date: Option[ZonedDateTime] = None,
  browserFamily: Option[String] = None,
  browserMajor: Option[String] = None,
  osFamily: Option[String] = None,
  deviceFamily: Option[String] = None,
  platform: Option[String] = None)

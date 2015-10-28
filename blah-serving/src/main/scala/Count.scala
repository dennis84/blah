package blah.serving

import org.joda.time.DateTime

case class Count(
  count: Long,
  date: Option[DateTime] = None,
  browserFamily: Option[String] = None,
  browserMajor: Option[String] = None,
  osFamily: Option[String] = None,
  deviceFamily: Option[String] = None,
  platform: Option[String] = None)

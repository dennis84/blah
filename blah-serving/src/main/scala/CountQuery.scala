package blah.serving

import org.joda.time.DateTime

case class CountQuery(
  page: Option[String] = None,
  from: Option[DateTime] = None,
  to: Option[DateTime] = None)

case class CountAllQuery(
  from: Option[DateTime] = None,
  to: Option[DateTime] = None)

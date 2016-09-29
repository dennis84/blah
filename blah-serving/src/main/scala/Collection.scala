package blah.serving

import java.time.ZonedDateTime

case class Collection(
  name: String,
  date: ZonedDateTime,
  count: Long = 0)

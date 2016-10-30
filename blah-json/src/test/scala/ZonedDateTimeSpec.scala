package blah.json

import org.scalatest._
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import spray.json._

class ZonedDateTimeSpec extends FlatSpec with Matchers {
  val dateStr = "2016-01-16T10:03:00.000Z"

  "A ZonedDateTime" should "replace joda.time" in {
    val date = ZonedDateTime.parse(dateStr)
    date.toString should be ("2016-01-16T10:03Z")
    date.truncatedTo(ChronoUnit.HOURS).toString should be ("2016-01-16T10:00Z")
    date.plusHours(2).toString should be ("2016-01-16T12:03Z")
    date.getYear should be (2016)
    date.getMonthValue should be (1)
    date.getDayOfMonth should be (16)
    date.toInstant.toEpochMilli should be (date.toInstant.toEpochMilli)
  }

  it should "be de/serializable" in {
    import TimeJsonProtocol._
    val date = ZonedDateTime.parse(dateStr)
    JsString(date.toString)
      .convertTo[ZonedDateTime]
      .toJson should be (JsString(date.toString))
  }
}

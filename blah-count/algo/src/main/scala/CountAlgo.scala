package blah.count

import java.util.UUID
import java.nio.ByteBuffer
import java.time.{ZonedDateTime, ZoneId}
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CountAlgo {
  def train(ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    ctx.sql("""|SELECT
               |  date,
               |  collection,
               |  props.item,
               |  props.userAgent,
               |  props.price
               |FROM events""".stripMargin)
      .filter("date is not null")
      .as[CountEvent]
      .map { event =>
        val ua = event.userAgent.map(UserAgent(_))
        val uac = ua.map(UserAgentClassifier.classify)
        val date = event.date.toInstant.atZone(ZoneId.of("UTC"))
        val doc = Count(
          collection = event.collection,
          date = date.plusHours(1)
                     .truncatedTo(ChronoUnit.HOURS)
                     .format(DateTimeFormatter.ISO_INSTANT),
          item = event.item,
          price = event.price,
          browserFamily = ua.map(_.browser.family),
          browserMajor = ua.map(_.browser.major).flatten,
          osFamily = ua.map(_.os.family),
          osMajor = ua.map(_.os.major).flatten,
          deviceFamily = ua.map(_.device.family),
          isMobile = uac.map(_.mobile),
          isTablet = uac.map(_.tablet),
          isMobileDevice = uac.map(_.mobileDevice),
          isComputer = uac.map(_.computer),
          platform = uac match {
            case Some(c) if c.mobile => Some("Mobile")
            case Some(c) if c.spider => Some("Spider")
            case _                   => Some("Computer")
          })
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt(doc.hashCode)
          .array)
        doc.copy(id = Some(uuid.toString))
      }
      .groupBy("id", "collection", "date", "item", "price", "browserFamily",
               "browserMajor", "osFamily", "osMajor", "deviceFamily",
               "isMobile", "isTablet", "isMobileDevice", "isComputer",
               "platform")
      .count()
      .as[Count]
  }
}

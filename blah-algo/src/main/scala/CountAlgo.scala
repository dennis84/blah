package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.temporal.ChronoUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import blah.core.{UserAgent, UserAgentClassifier}

class CountAlgo extends Algo[Count] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(CountSchema())
    reader.json(rdd).registerTempTable("count")
    ctx.sql("""|SELECT
               |  date,
               |  collection,
               |  props.item,
               |  props.userAgent,
               |  props.price
               |FROM count""".stripMargin)
      .filter("date is not null")
      .map(CountEvent(_))
      .map { event =>
        val ua = event.userAgent.map(UserAgent(_))
        val uac = ua.map(UserAgentClassifier.classify)
        val doc = Count(
          collection = event.collection,
          date = event.date.truncatedTo(ChronoUnit.HOURS).toString,
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
        ((uuid.toString, doc), 1)
      }
      .reduceByKey(_ + _)
      .map { case((id, count), nb) => (id, count.copy(count = nb)) }
  }
}

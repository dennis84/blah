package blah.algo

import java.security.MessageDigest
import java.time.temporal.ChronoUnit
import java.time.ZonedDateTime
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Row}
import org.apache.spark.sql.types.{StructType,StructField,StringType}
import blah.core.{UserAgent, UserAgentClassifier}

case class Count(
  collection: String,
  date: String,
  browserFamily: String,
  browserMajor: String,
  osFamily: String,
  osMajor: String,
  deviceFamily: String,
  isMobile: Boolean,
  isTablet: Boolean,
  isMobileDevice: Boolean,
  isComputer: Boolean,
  platform: String,
  count: Long = 0)

case class CountEvent(
  date: String,
  collection: String,
  item: Option[String] = None,
  userAgent: Option[String] = None)

object CountEvent {
  def apply(r: Row): CountEvent = CountEvent(
    date = ZonedDateTime.parse(r.getString(0)).truncatedTo(ChronoUnit.HOURS).toString,
    collection = r.getString(1),
    item = Option(r.getString(2)),
    userAgent = Option(r.getString(3)))
}

object CountSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("collection", StringType, true),
    StructField("props", StructType(Array(
      StructField("item", StringType, true),
      StructField("userAgent", StringType, true))), true)))
}

class CountAlgo {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    val reader = ctx.read.schema(CountSchema())
    reader.json(rdd).registerTempTable("count")
    ctx.sql("""|SELECT
               |  date,
               |  collection,
               |  props.item,
               |  props.userAgent
               |FROM count""".stripMargin)
      .map(CountEvent(_))
      .map(x => (x, 1))
      .reduceByKey(_ + _)
      .collect { case(event, count) =>
        val ua = event.userAgent.map(UserAgent(_))
        val uac = ua.map(UserAgentClassifier.classify)
        val doc = Count(
          count = count,
          collection = event.collection,
          date = event.date,
          browserFamily = ua.map(_.browser.family).getOrElse("N/A"),
          browserMajor = ua.map(_.browser.major).flatten.getOrElse("N/A"),
          osFamily = ua.map(_.os.family).getOrElse("N/A"),
          osMajor = ua.map(_.os.major).flatten.getOrElse("N/A"),
          deviceFamily = ua.map(_.device.family).getOrElse("N/A"),
          isMobile = uac.map(_.mobile).getOrElse(false),
          isTablet = uac.map(_.tablet).getOrElse(false),
          isMobileDevice = uac.map(_.mobileDevice).getOrElse(false),
          isComputer = uac.map(_.computer).getOrElse(true),
          platform = uac.map {
            case c if c.mobile => "Mobile"
            case c if c.spider => "Spider"
            case _             => "Computer"
          }.getOrElse("Computer"))
        val id = MessageDigest.getInstance("SHA-1")
          .digest(doc.hashCode.toString.getBytes("UTF-8"))
          .map("%02x".format(_))
          .mkString
        (id, doc)
      }
  }
}

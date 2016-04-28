package blah.algo

import java.security.MessageDigest
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import blah.core.{UserAgent, UserAgentClassifier}

class CountAlgo extends Algo[Count] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(CountSchema())
    reader.json(rdd).registerTempTable("count")
    val output = ctx.sql("""|SELECT
                            |  date,
                            |  collection,
                            |  props.item,
                            |  props.userAgent
                            |FROM count""".stripMargin)
      .map(CountEvent(_))
      .map { event =>
        val ua = event.userAgent.map(UserAgent(_))
        val uac = ua.map(UserAgentClassifier.classify)
        val doc = Count(
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
        ((id, doc), 1)
      }
      .reduceByKey(_ + _)
      .map { case((id, count), nb) => (id, count.copy(count = nb)) }
    Result(output, output.map(_._2).toDF)
  }
}

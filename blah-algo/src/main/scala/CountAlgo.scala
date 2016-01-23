package blah.algo

import java.security.MessageDigest
import java.time.temporal.ChronoUnit
import scala.util.Try
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._          
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgo extends Algo {
  def train(rdd: RDD[String]) = rdd
    .map(x => Try(x.parseJson.convertTo[Event]))
    .filter(_.isSuccess)
    .map(_.get)
    .map { event =>
      val props = event.props collect {
        case (k, JsString(v)) => (k, v)
        case (k, JsNumber(v)) => (k, v)
      }
      val ua = props.get("userAgent") collect {
        case x: String => UserAgent(x)
      }
      val uac = ua.map(UserAgentClassifier.classify)
      val doc = props ++ Map(
        "collection" -> event.collection,
        "date" -> event.date.truncatedTo(ChronoUnit.HOURS).toString,
        "browserFamily" -> ua.map(_.browser.family).getOrElse("N/A"),
        "browserMajor" -> ua.map(_.browser.major).flatten.getOrElse("N/A"),
        "browserMinor" -> ua.map(_.browser.minor).flatten.getOrElse("N/A"),
        "browserPatch" -> ua.map(_.browser.patch).flatten.getOrElse("N/A"),
        "osFamily" -> ua.map(_.os.family).getOrElse("N/A"),
        "osMajor" -> ua.map(_.os.major).flatten.getOrElse("N/A"),
        "osMinor" -> ua.map(_.os.minor).flatten.getOrElse("N/A"),
        "osPatch" -> ua.map(_.os.patch).flatten.getOrElse("N/A"),
        "deviceFamily" -> ua.map(_.device.family).getOrElse("N/A"),
        "isMobile" -> uac.map(_.mobile).getOrElse(false),
        "isTablet" -> uac.map(_.tablet).getOrElse(false),
        "isMobileDevice" -> uac.map(_.mobileDevice).getOrElse(false),
        "isComputer" -> uac.map(_.computer).getOrElse(true),
        "platform" -> uac.map {
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
    .map(x => Doc(x._1._1, x._1._2 ++ Map("count" -> x._2)))
}

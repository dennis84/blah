package blah.algo

import java.security.MessageDigest
import scala.util.Try
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._          
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgo extends Algo {
  def train(rdd: RDD[String]) {
    val events = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map { view =>
        val ua = view.props.userAgent.map(x => UserAgent(x))
        val doc = Map(
          "page" -> view.props.page,
          "date" -> view.date.hourOfDay.roundFloorCopy.toString,
          "browserFamily" -> ua.map(_.browser.family),
          "browserMajor" -> ua.map(_.browser.major).flatten,
          "browserMinor" -> ua.map(_.browser.minor).flatten,
          "browserPatch" -> ua.map(_.browser.patch).flatten,
          "osFamily" -> ua.map(_.os.family),
          "osMajor" -> ua.map(_.os.major).flatten,
          "osMinor" -> ua.map(_.os.minor).flatten,
          "osPatch" -> ua.map(_.os.patch).flatten,
          "deviceFamily" -> ua.map(_.device.family))
        val id = MessageDigest.getInstance("SHA-1")
          .digest(doc.hashCode.toString.getBytes("UTF-8"))
          .map("%02x".format(_))
          .mkString
        ((id, doc), 1)
      }
      .reduceByKey(_ + _)
      .map(x => (Map(ID -> x._1._1), x._1._2 ++ Map("count" -> x._2)))

    events.saveToEsWithMeta("blah/count")
  }
}

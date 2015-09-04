package blah.algo

import scala.util.Try
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgo extends Algo {
  def train(rdd: RDD[String]) {
    val events: RDD[Count] = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map { view =>
        val ua = view.props.userAgent.map(x => UserAgent(x))
        (Count(
          view.props.page,
          view.date.hourOfDay.roundFloorCopy,
          ua.map(_.browser.family),
          ua.map(_.browser.major).flatten,
          ua.map(_.browser.minor).flatten,
          ua.map(_.os.family),
          ua.map(_.os.major).flatten,
          ua.map(_.os.minor).flatten,
          ua.map(_.device.family)
        ), 1L)
      }
      .reduceByKey(_ + _)
      .map(x => x._1.copy(count = Some(x._2)))

    events.saveToCassandra("blah", "count")
  }
}

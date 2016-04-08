package blah.algo

import scala.util.Try
import org.apache.spark.rdd.RDD
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgo extends Algo {
  def train(rdd: RDD[String], args: Array[String]) = rdd
    .map(x => Try(x.parseJson.convertTo[UserEvent]))
    .filter(_.isSuccess)
    .map(_.get)
    .map(x => (x.props.user, (x.props.ip, x.date, x.props.item, x.props.title)))
    .groupByKey
    .map { case(u, values) =>
      val (maybeIp, date, item, title) = values.last
      val geo = maybeIp.map(GeoIp.find _).flatten
      val userEvents = (values takeRight 20).toList.reverse map {
        case (maybeIp, date, maybeItem, maybeTitle) => Map(
          "ip" -> maybeIp,
          "date" -> date.toString,
          "item" -> maybeItem,
          "title" -> maybeTitle
        )
      }

      Doc(u, Map(
        "user" -> u,
        "date" -> date.toString,
        "lng" -> geo.map(_.lng).getOrElse(0),
        "lat" -> geo.map(_.lat).getOrElse(0),
        "country" -> geo.map(_.country).flatten.getOrElse("N/A"),
        "countryCode" -> geo.map(_.countryCode).flatten.getOrElse("N/A"),
        "city" -> geo.map(_.city).flatten.getOrElse("N/A"),
        "zipCode" -> geo.map(_.zipCode).flatten.getOrElse("N/A"),
        "events" -> userEvents
      ))
    }
}

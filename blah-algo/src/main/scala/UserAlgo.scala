package blah.algo

import scala.util.Try
import org.apache.spark.rdd.RDD
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgo extends Algo {
  def train(rdd: RDD[String]) = rdd
    .map(x => Try(x.parseJson.convertTo[UserEvent]))
    .filter(_.isSuccess)
    .map(_.get)
    .map(x => (x.props.user, (x.props.ip, x.date)))
    .groupByKey
    .map { case(u, values) =>
      val (maybeIp, date) = values.last
      val geo = maybeIp.map(GeoIp.find _).flatten
      Doc(u, Map(
        "user" -> u,
        "date" -> date.toString,
        "lng" -> geo.map(_.lng).getOrElse(0),
        "lat" -> geo.map(_.lat).getOrElse(0),
        "country" -> geo.map(_.country).flatten.getOrElse("N/A"),
        "countryCode" -> geo.map(_.countryCode).flatten.getOrElse("N/A"),
        "city" -> geo.map(_.city).flatten.getOrElse("N/A"),
        "zipCode" -> geo.map(_.zipCode).flatten.getOrElse("N/A")
      ))
    }
}

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
      val geoData = (for {
        ip <- maybeIp
        data <- GeoIp.find(ip)
      } yield Map(
        "lng" -> data.lng,
        "lat" -> data.lat,
        "country" -> data.country,
        "countryCode" -> data.countryCode,
        "city" -> data.city,
        "zipCode" -> data.zipCode
      )) getOrElse Map.empty

      Doc(u, Map(
        "user" -> u,
        "ip" -> maybeIp,
        "date" -> date.toString
      ) ++ geoData)
    }
}

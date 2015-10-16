package blah.algo

import scala.util.Try
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgo extends Algo {
  def train(rdd: RDD[String]) {
    val events = rdd
      .map(x => Try(x.parseJson.convertTo[UserEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map { event =>
        (event.props.user, event.props.ip)
      }
      .groupByKey
      .map { case(u, ips) =>
        val maybeIp = ips.flatten.lastOption
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
        (Map(ID -> u), Map("user" -> u, "ip" -> maybeIp) ++ geoData)
      }

    events.saveToEsWithMeta("blah/users")
  }
}

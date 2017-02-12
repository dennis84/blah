package blah.user

import scala.util.{Try, Success, Failure}
import java.net.InetAddress
import com.maxmind.geoip2.DatabaseReader

case class GeoIp(
  lng: Double,
  lat: Double,
  country: Option[String] = None,
  countryCode: Option[String] = None,
  city: Option[String] = None,
  zipCode: Option[String] = None)

object GeoIp {
  private val reader = new DatabaseReader.Builder(
    getClass.getResourceAsStream("/GeoLite2-City.mmdb")).build

  def find(ip: String): Option[GeoIp] =
    Try(reader.city(InetAddress getByName ip)) match {
      case Success(r) => Some(GeoIp(
        lng = r.getLocation.getLongitude,
        lat = r.getLocation.getLatitude,
        country = Option(r.getCountry.getName),
        countryCode = Option(r.getCountry.getIsoCode),
        city = Option(r.getCity.getName),
        zipCode = Option(r.getPostal.getCode)))
      case Failure(e) => None
    }
}

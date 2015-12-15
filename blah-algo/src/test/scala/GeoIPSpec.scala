package blah.algo

import org.scalatest._

class GeoIPSpec extends FlatSpec with Matchers {

  "GeoIP" should "find 127.0.0.1" in {
    GeoIp.find("127.0.0.1") should be (None)
  }

  it should "find 8.8.8.8" in {
    val res = GeoIp.find("8.8.8.8")
    res.isDefined should be (true)
    res.get.country should be (Some("United States"))
    res.get.city should be (Some("Mountain View"))
  }
}

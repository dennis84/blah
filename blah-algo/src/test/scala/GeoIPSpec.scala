package blah.algo

import org.scalatest._

class GeoIPSpec extends FlatSpec with Matchers {

  "GeoIP" should "find 127.0.0.1" in {
    GeoIp.find("127.0.0.1") should be (None)
  }

  it should "find 128.101.101.101" in {
    println(GeoIp.find("128.101.101.101"))
  }
}

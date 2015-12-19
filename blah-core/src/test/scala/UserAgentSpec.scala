package blah.core

import org.scalatest._

class UserAgentSpec extends FlatSpec with Matchers {

  val h1 = """|Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5)
              |AppleWebKit/537.36 (KHTML, like Gecko)
              |Chrome/47.0.2496.0
              |Safari/537.36""".stripMargin

  "UserAgent" should "parse from empty string" in {
    UserAgent("") should be(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("Other")
    ))
  }

  it should "parse from UA" in {
    UserAgent(h1) should be(UserAgent(
      Browser("Chrome", Some("47"), Some("0"), Some("2496")),
      OS("Mac OS X", Some("10"), Some("10"), Some("5")),
      Device("Other")
    ))
  }

  it should "json roundtrip" in {
    import spray.json._
    import JsonProtocol._
    val ua = UserAgent(h1)
    val json = ua.toJson.compactPrint
    ua should be (json.parseJson.convertTo[UserAgent])
  }
}

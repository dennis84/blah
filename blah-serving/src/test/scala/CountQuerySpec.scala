package blah.serving

import org.scalatest._
import spray.json._
import ServingJsonProtocol._

class CountQuerySpec extends FlatSpec with Matchers {

  "CountQuery" should "from json" in {
    val json = """|{
                  |  "filterBy": {
                  |    "page": "home",
                  |    "user_agent.device.family": "iPhone"
                  |  },
                  |  "groupBy": [
                  |    "date.hour"
                  |  ]
                  |}""".stripMargin
    json.parseJson.convertTo[CountQuery] should be(CountQuery(
      Some(Map("page" -> "home", "user_agent.device.family" -> "iPhone")),
      Some(List("date.hour"))))
  }
}

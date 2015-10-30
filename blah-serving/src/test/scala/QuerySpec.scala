package blah.serving

import org.scalatest._
import spray.json._
import ServingJsonProtocol._

class CountQuerySpec extends FlatSpec with Matchers {

  "Query" should "from json" in {
    val json = """|{
                  |  "filterBy": [
                  |    {"prop": "page", "operator": "eq", "value": "home"},
                  |    {"prop": "user_agent.device.family", "operator": "eq", "value": "iPhone"}
                  |  ],
                  |  "groupBy": [
                  |    "date.hour"
                  |  ]
                  |}""".stripMargin
    json.parseJson.convertTo[Query] should be(Query(
      Some(List(Filter("page", "eq", "home"), Filter("user_agent.device.family", "eq", "iPhone"))),
      Some(List("date.hour"))))
  }
}

package blah.serving

import org.scalatest._
import spray.json._
import ServingJsonProtocol._

class QuerySpec extends FlatSpec with Matchers {
  "Query" should "from json" in {
    val json = """|{
                  |  "filterBy": {
                  |    "page": "home",
                  |    "user_agent.device.family": "iPhone"
                  |  },
                  |  "groupBy": [
                  |    "date.hour"
                  |  ]
                  |}""".stripMargin
    json.parseJson.convertTo[Query] should be(Query(
      Some(Map("page" -> "home", "user_agent.device.family" -> "iPhone")),
      Some(List("date.hour"))))
  }

  it should "to cql query" in {
    val q = Query(Some(Map(
      "page" -> "home",
      "user_agent.device.family" -> "iPhone",
      "date.from" -> "2015-09-02",
      "date.to" -> "2015-09-04"
    )), Some(List("date.hour")))

    println(QueryToCQL(q))
  }
}

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

  it should "to es query" in {
    val q = Query(Some(Map(
      "page" -> "home",
      "user_agent.device.family" -> "iPhone",
      "user_agent.browser.family" -> "Chrome",
      "user_agent.browser.major" -> "47",
      "date.from" -> "2015-09-02",
      "date.to" -> "2015-09-04"
    )), Some(List("date.hour")))

    q.toEs.parseJson should be(JsObject(
      "filter" -> JsObject(
        "range" -> JsObject(
          "date" -> JsObject(
            "gte" -> JsString("2015-09-02"),
            "lte" -> JsString("2015-09-04")
      ))),
      "query" -> JsObject(
        "bool" -> JsObject(
          "must" -> JsArray(Vector(
            JsObject("match" -> JsObject("page" -> JsString("home"))),
            JsObject("match" -> JsObject("deviceFamily" -> JsString("iPhone"))),
            JsObject("match" -> JsObject("browserFamily" -> JsString("Chrome"))),
            JsObject("match" -> JsObject("browserMajor" -> JsString("47")))
      ))))
    ))
  }
}

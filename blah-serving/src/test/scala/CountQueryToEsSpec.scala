package blah.serving

import org.scalatest._
import spray.json._

class CountQueryToEsSpec extends FlatSpec with Matchers {

  "CountQueryToEs" should "convert filters to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home",
      "user_agent.device.family" -> "iPhone",
      "user_agent.browser.family" -> "Chrome",
      "user_agent.browser.major" -> "47",
      "date.from" -> "2015-09-02",
      "date.to" -> "2015-09-04"
    )))

    CountQueryToEs.filtered(q) should be(JsObject(
      "query" -> JsObject(
        "filtered" -> JsObject(
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
      )),
      "aggs" -> JsObject("count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))))
    ))
  }

  it should "convert filters and empty groups to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home"
    )), Some(Nil))

    CountQueryToEs.grouped(q) should be(JsObject(
      "query" -> JsObject(
        "filtered" -> JsObject(
          "query" -> JsObject(
            "bool" -> JsObject(
              "must" -> JsArray(Vector(
                JsObject("match" -> JsObject("page" -> JsString("home")))
      )))))),
      "aggs" -> JsObject(
        "date" -> JsObject(
          "date_histogram" -> JsObject(
            "field" -> JsString("date"),
            "interval" -> JsString("day")),
          "aggs" -> JsObject(
            "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))))
      ))
    ))
  }

  it should "convert filters and groups to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home"
    )), Some(List(
      "date.hour",
      "user_agent.browser.family",
      "user_agent.os.family"
    )))

    CountQueryToEs.grouped(q) should be(JsObject(
      "query" -> JsObject(
        "filtered" -> JsObject(
          "query" -> JsObject(
            "bool" -> JsObject(
              "must" -> JsArray(Vector(
                JsObject("match" -> JsObject("page" -> JsString("home")))
      )))))),
      "aggs" -> JsObject(
        "date" -> JsObject(
          "date_histogram" -> JsObject(
            "field" -> JsString("date"),
            "interval" -> JsString("hour")),
          "aggs" -> JsObject(
            "browserFamily" -> JsObject(
              "terms" -> JsObject("field" -> JsString("browserFamily")),
              "aggs" -> JsObject(
                "osFamily" -> JsObject(
                  "terms" -> JsObject("field" -> JsString("osFamily")),
                  "aggs" -> JsObject(
                    "count" -> JsObject("sum" -> JsObject("field" -> JsString("count")))))
            )))
      ))
    ))
  }
}

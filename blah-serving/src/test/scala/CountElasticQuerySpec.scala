package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class CountElasticQuerySpec extends FlatSpec with Matchers {

  "CountElasticQuery" should "convert filters to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home",
      "user_agent.device.family" -> "iPhone",
      "user_agent.browser.family" -> "Chrome",
      "user_agent.browser.major" -> "47",
      "date.from" -> "2015-09-02",
      "date.to" -> "2015-09-04"
    )))

    CountElasticQuery.filtered(q) should be(
      ("query" -> ("filtered" ->
        ("filter" -> ("range" -> ("date" ->
          ("gte" -> "2015-09-02") ~
          ("lte" -> "2015-09-04")
        ))) ~
        ("query" -> ("bool" -> ("must" -> List(
          ("match" -> ("page" -> "home")),
          ("match" -> ("deviceFamily" -> "iPhone")),
          ("match" -> ("browserFamily" -> "Chrome")),
          ("match" -> ("browserMajor" -> "47"))
        ))))
      )) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    )
  }

  // it should "convert filters and empty groups to es" in {
  //   val q = CountQuery(Some(Map(
  //     "page" -> "home"
  //   )), Some(Nil))

  //   CountElasticQuery.grouped(q) should be(JsObject(
  //     "query" -> JsObject(
  //       "filtered" -> JsObject(
  //         "query" -> JsObject(
  //           "bool" -> JsObject(
  //             "must" -> JsArray(Vector(
  //               JsObject("match" -> JsObject("page" -> JsString("home")))
  //     )))))),
  //     "aggs" -> JsObject(
  //       "date" -> JsObject(
  //         "date_histogram" -> JsObject(
  //           "field" -> JsString("date"),
  //           "interval" -> JsString("day")),
  //         "aggs" -> JsObject(
  //           "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))))
  //     ))
  //   ))
  // }

  // it should "convert filters and groups to es" in {
  //   val q = CountQuery(Some(Map(
  //     "page" -> "home"
  //   )), Some(List(
  //     "date.hour",
  //     "user_agent.browser.family",
  //     "user_agent.os.family"
  //   )))

  //   println(CountElasticQuery.grouped(q).prettyPrint)
  //   CountElasticQuery.grouped(q) should be(JsObject(
  //     "query" -> JsObject(
  //       "filtered" -> JsObject(
  //         "query" -> JsObject(
  //           "bool" -> JsObject(
  //             "must" -> JsArray(Vector(
  //               JsObject("match" -> JsObject("page" -> JsString("home")))
  //     )))))),
  //     "aggs" -> JsObject(
  //       "date" -> JsObject(
  //         "date_histogram" -> JsObject(
  //           "field" -> JsString("date"),
  //           "interval" -> JsString("hour")),
  //         "aggs" -> JsObject(
  //           "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))),
  //           "browserFamily" -> JsObject(
  //             "terms" -> JsObject("field" -> JsString("browserFamily")),
  //             "aggs" -> JsObject(
  //               "count" -> JsObject("sum" -> JsObject("field" -> JsString("count"))),
  //               "osFamily" -> JsObject(
  //                 "terms" -> JsObject("field" -> JsString("osFamily")),
  //                 "aggs" -> JsObject(
  //                   "count" -> JsObject("sum" -> JsObject("field" -> JsString("count")))))
  //           ))
  //       )
  //     ))
  //   ))
  // }
}

package blah.serving

import org.scalatest._
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
          ("term" -> ("page" -> "home")),
          ("term" -> ("deviceFamily" -> "iPhone")),
          ("term" -> ("browserFamily" -> "Chrome")),
          ("term" -> ("browserMajor" -> "47"))
        ))))
      )) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    )
  }

  it should "convert filters and empty groups to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home"
    )), Some(Nil))

    CountElasticQuery.grouped(q) should be(
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("page" -> "home"))
      )))))) ~
      ("aggs" -> ("date" ->
        ("date_histogram" ->
          ("field" -> "date") ~
          ("interval" -> "day")) ~
        ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
      ))
    )
  }

  it should "convert filters and groups to es" in {
    val q = CountQuery(Some(Map(
      "page" -> "home"
    )), Some(List(
      "date.hour",
      "user_agent.browser.family",
      "user_agent.os.family"
    )))

    CountElasticQuery.grouped(q) should be(
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("page" -> "home"))
      )))))) ~
      ("aggs" -> (
        ("date" ->
          ("date_histogram" ->
            ("field" -> "date") ~
            ("interval" -> "hour")) ~
          ("aggs" ->
            ("count" -> ("sum" -> ("field" -> "count"))) ~
            ("browserFamily" ->
              ("terms" -> ("field" -> "browserFamily")) ~
              ("aggs" ->
                ("count" -> ("sum" -> ("field" -> "count"))) ~
                ("osFamily" ->
                  ("terms" -> ("field" -> "osFamily")) ~
                  ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
                )))))))
    )
  }
}

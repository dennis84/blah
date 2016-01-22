package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class CountElasticQuerySpec extends FlatSpec with Matchers {

  "The CountElasticQuery" should "convert an empty query object to json" in {
    val empty: JsObject =
      ("query" -> ("filtered" ->
        ("query" -> ("bool" -> ("must" -> List(
          "term" -> ("collection" -> "pageviews"))))))) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
    CountElasticQuery(CountQuery("pageviews", None, None)) should be (empty)
    CountElasticQuery(CountQuery("pageviews", Some(Nil), None)) should be (empty)
  }

  it should "convert a query with filters to json" in {
    val query = CountQuery("pageviews", Some(List(
      Filter("item", "eq", "home"),
      Filter("user_agent.device.family", "eq", "iPhone"),
      Filter("user_agent.browser.family", "eq", "Chrome"),
      Filter("user_agent.browser.major", "eq", "47"),
      Filter("date.from", "gte", "2015-09-02"),
      Filter("date.to", "lte", "2015-09-04")
    )))

    val json: JsObject =
      ("query" -> ("filtered" ->
        ("filter" -> ("range" -> ("date" ->
          ("gte" -> "2015-09-02") ~
          ("lte" -> "2015-09-04")
        ))) ~
        ("query" -> ("bool" -> ("must" -> List(
          ("term" -> ("collection" -> "pageviews")),
          ("term" -> ("item" -> "home")),
          ("term" -> ("deviceFamily" -> "iPhone")),
          ("term" -> ("browserFamily" -> "Chrome")),
          ("term" -> ("browserMajor" -> "47"))
        ))))
      )) ~
      ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))

    CountElasticQuery(query) should be (json)
  }

  it should "convert a query with filters and empty groups to json" in {
    val query = CountQuery("pageviews", Some(List(
      Filter("item", "eq", "home")
    )), Some(Nil))

    val json: JsObject =
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("collection" -> "pageviews")),
        ("term" -> ("item" -> "home"))
      )))))) ~
      ("aggs" -> ("date" ->
        ("date_histogram" ->
          ("field" -> "date") ~
          ("interval" -> "day")) ~
        ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
      ))

    CountElasticQuery(query) should be (json)
  }

  it should "convert a query with filters and groups to json" in {
    val query = CountQuery("pageviews", Some(List(
      Filter("item", "eq", "home")
    )), Some(List(
      "date.hour",
      "user_agent.browser.family",
      "user_agent.os.family"
    )))

    val json: JsObject =
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("collection" -> "pageviews")),
        ("term" -> ("item" -> "home"))
      )))))) ~
      ("aggs" ->
        ("date" ->
          ("date_histogram" ->
            ("field" -> "date") ~
            ("interval" -> "hour")) ~
          ("aggs" ->
            ("count" -> ("sum" -> ("field" -> "count"))) ~
            ("browserFamily" ->
              ("terms" -> ("field" -> "browserFamily") ~ ("size" -> 0)) ~
              ("aggs" ->
                ("count" -> ("sum" -> ("field" -> "count"))) ~
                ("osFamily" ->
                  ("terms" -> ("field" -> "osFamily") ~ ("size" -> 0)) ~
                  ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
                ))))))

    CountElasticQuery(query) should be (json)
  }
}

package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class SumElasticQuerySpec extends FlatSpec with Matchers {

  "The SumElasticQuery" should "convert an empty query object to json" in {
    val empty: JsObject =
      ("query" -> ("filtered" ->
        ("query" -> ("bool" -> ("must" -> List(
          "term" -> ("collection" -> "purchases"))))))) ~
      ("aggs" -> ("sum" -> ("sum" -> ("field" -> "price"))))
    SumElasticQuery(SumQuery("purchases", "price", None)) should be (empty)
  }

  it should "convert a query with filters to json" in {
    val query = SumQuery("purchases", "price", Some(List(
      Filter("date.from", "gte", "2016"),
      Filter("date.to", "lte", "2017")
    )))

    val json: JsObject =
      ("query" -> ("filtered" ->
        ("filter" -> ("range" -> ("date" ->
          ("gte" -> "2016") ~
          ("lte" -> "2017")
        ))) ~
        ("query" -> ("bool" -> ("must" -> List(
          ("term" -> ("collection" -> "purchases"))
        ))))
      )) ~
      ("aggs" -> ("sum" -> ("sum" -> ("field" -> "price"))))
    SumElasticQuery(query) should be (json)
  }
}

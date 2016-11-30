package blah.serving

import org.scalatest._
import spray.json._
import blah.json.JsonDsl._

class SumElasticQuerySpec extends FlatSpec with Matchers {

  "The SumElasticQuery" should "convert an empty query object to json" in {
    val empty: JsObject =
      ("query" -> ("bool" ->
        ("must" -> List("term" -> ("collection" -> "buy"))))) ~
      ("aggs" -> ("sum" -> ("sum" -> ("script" -> "doc['price'].value * doc['count'].value"))))
    SumElasticQuery(SumQuery("buy", "price", None)) should be (empty)
  }

  it should "convert a query with filters to json" in {
    val query = SumQuery("buy", "price", Some(List(
      Filter("date.from", "gte", "2016"),
      Filter("date.to", "lte", "2017")
    )))

    val json: JsObject =
      ("query" -> ("bool" ->
        ("filter" -> ("range" -> ("date" ->
          ("gte" -> "2016") ~
          ("lte" -> "2017")
        ))) ~
        ("must" -> List("term" -> ("collection" -> "buy")))
      )) ~
      ("aggs" -> ("sum" -> ("sum" -> ("script" -> "doc['price'].value * doc['count'].value"))))
    SumElasticQuery(query) should be (json)
  }
}

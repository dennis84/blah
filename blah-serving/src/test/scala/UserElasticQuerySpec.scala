package blah.serving

import org.scalatest._
import spray.json._
import blah.json.JsonDsl._

class UserElasticQuerySpec extends FlatSpec with Matchers {

  "The UserElasticQuery" should "convert an empty query object to json" in {
    val empty: JsObject = ("query" -> ("match_all" -> JsObject.empty))
    UserElasticQuery(UserQuery(None, None)) should be (empty)
    UserElasticQuery(UserQuery(Some(Nil), None)) should be (empty)
  }

  it should "convert a query with groups to json" in {
    val query = UserQuery(None, Option(List("country")))
    val json: JsObject =
      ("aggs" -> ("country" -> ("terms" -> 
        ("field" -> "country") ~
        ("size" -> 0)
      )))

    UserElasticQuery(query) should be (json)
  }

  it should "convert a query with filters to json" in {
    val query = UserQuery(Some(List(
      Filter("date.from", "gte", "2016-01-01")
    )), Some(Nil))

    val json: JsObject =
      ("query" -> ("filtered" -> ("filter" ->
        ("range" -> ("date" -> ("gte" -> "2016-01-01"))))))

    UserElasticQuery(query) should be (json)
  }

  it should "convert a query with filters and groups to json" in {
    val query = UserQuery(Some(List(
      Filter("date.from", "gte", "2016-01-01")
    )), Option(List("country")))

    val json: JsObject =
      ("query" -> ("filtered" -> ("filter" ->
        ("range" -> ("date" -> ("gte" -> "2016-01-01")))))) ~
      ("aggs" -> ("country" -> ("terms" -> 
        ("field" -> "country") ~
        ("size" -> 0)
      )))

    UserElasticQuery(query) should be (json)
  }
}

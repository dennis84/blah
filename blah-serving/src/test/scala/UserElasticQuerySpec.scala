package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class UserElasticQuerySpec extends FlatSpec with Matchers {

  "The UserElasticQuery" should "convert an empty query object to json" in {
    UserElasticQuery(Query(None, None)) should be (JsObject())
    UserElasticQuery(Query(Some(Nil), None)) should be (JsObject())
  }

  it should "convert a query with groups to json" in {
    val query = Query(None, Option(List("country")))
    val json: JsObject = ("aggs" -> ("country" -> ("terms" -> ("field" -> "country"))))
    UserElasticQuery(query) should be (json)
  }
}

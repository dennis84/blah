package blah.serving

import org.scalatest._
import spray.json._

class UserElasticQuerySpec extends FlatSpec with Matchers {

  "UserElasticQuery" should "group empty query object" in {
    UserElasticQuery(Query(None, None)) should be(JsObject())
  }

  it should "group" in {
    val query = Query(None, Option(List("country")))
    UserElasticQuery(query) should be(JsObject("aggs" -> JsObject(
      "country" -> JsObject("terms" -> JsObject("field" -> JsString("country"))))))
  }
}

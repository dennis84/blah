package blah.serving

import org.scalatest._
import spray.json._

class UserElasticQuerySpec extends FlatSpec with Matchers {

  "UserElasticQuery" should "group nothing" in {
    val query = UserQuery(None, None)
    UserElasticQuery.grouped(query) should be(JsObject())
  }

  it should "group" in {
    val query = UserQuery(None, Option(List("country")))
    UserElasticQuery.grouped(query) should be(JsObject("aggs" -> JsObject(
      "country" -> JsObject("terms" -> JsObject("field" -> JsString("country"))))))
  }
}

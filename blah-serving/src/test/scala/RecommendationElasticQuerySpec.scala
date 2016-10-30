package blah.serving

import org.scalatest._
import spray.json._
import blah.json.JsonDsl._

class RecommendationElasticQuerySpec extends FlatSpec with Matchers {

  "The RecommendationElasticQuery" should "to json" in {
    val expected: JsObject =
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("user" -> "dennis"))
      ))))))

    val q = RecommendationElasticQuery(RecommendationQuery(
      user = "dennis"
    )) should be (expected)
  }

  it should "to json with collection" in {
    val expected: JsObject =
      ("query" -> ("filtered" -> ("query" -> ("bool" -> ("must" -> List(
        ("term" -> ("user" -> "dennis")),
        ("term" -> ("collection" -> "buy"))
      ))))))

    val q = RecommendationElasticQuery(RecommendationQuery(
      user = "dennis",
      collection = Some("buy")
    )) should be (expected)
  }
}

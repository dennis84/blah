package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class MostViewedElasticQuerySpec extends FlatSpec with Matchers {

  "The MostViewedElasticQuery" should "works" in {
    val q = MostViewedQuery("pageviews", 10)
    MostViewedElasticQuery(q) should be (
      ("query" -> ("filtered" ->
        ("query" -> ("bool" -> ("must" -> List(
          "term" -> ("collection" -> "pageviews"))))))) ~
      ("aggs" -> ("item" ->
        ("terms" ->
          ("field" -> "item") ~
          ("size" -> 0) ~
          ("order" -> ("count" -> "desc"))) ~
        ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
      )))
  }
}

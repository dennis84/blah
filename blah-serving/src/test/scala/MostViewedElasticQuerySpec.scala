package blah.serving

import org.scalatest._
import spray.json._
import blah.core.JsonDsl._

class MostViewedElasticQuerySpec extends FlatSpec with Matchers {

  "The MostViewedElasticQuery" should "works" in {
    val q = MostViewedQuery("view", Some(10))
    MostViewedElasticQuery(q) should be (
      ("query" -> ("filtered" ->
        ("query" -> ("bool" -> ("must" -> List(
          "term" -> ("collection" -> "view"))))))) ~
      ("aggs" -> ("item" ->
        ("terms" ->
          ("field" -> "item") ~
          ("size" -> 10) ~
          ("order" -> ("count" -> "desc"))) ~
        ("aggs" -> ("count" -> ("sum" -> ("field" -> "count"))))
      )))
  }
}

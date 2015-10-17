package blah.serving

import org.scalatest._
import spray.json._
import spray.json.lenses.JsonLenses._
import ServingJsonProtocol._

class UserResponseParserSpec extends FlatSpec with Matchers {

  val resp = """|{
                |  "took":2,
                |  "timed_out":false,
                |  "_shards":{
                |    "total":5,
                |    "successful":5,
                |    "failed":0
                |  },
                |  "hits":{
                |    "total":4,
                |    "max_score":0.0,
                |    "hits":[
                |
                |    ]
                |  },
                |  "aggregations":{
                |    "country":{
                |      "doc_count_error_upper_bound":0,
                |      "sum_other_doc_count":0,
                |      "buckets":[
                |        {
                |          "key":"Germany",
                |          "doc_count":3
                |        },
                |        {
                |          "key":"United States",
                |          "doc_count":1
                |        }
                |      ]
                |    }
                |  }
                |}""".stripMargin

  "UserResponseParser" should "parse an aggregated response" in {
    val json = resp.parseJson
    val aggs = json.extract[JsValue]('aggregations)
    val groups = List("country")
    UserResponseParser.parse(groups, aggs) should be (Vector(
      JsObject(
        "count" -> JsNumber(3),
        "country" -> JsString("Germany")),
      JsObject(
        "count" -> JsNumber(1),
        "country" -> JsString("United States"))
    ))
  }
}

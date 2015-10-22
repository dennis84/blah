package blah.serving

import org.scalatest._
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.AggregationParser
import ServingJsonProtocol._

class ParseUserResponseSpec extends FlatSpec with Matchers {

  val resp = """|{
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

  "AggregationParser" should "parse a user response" in {
    val json = resp.parseJson
    val aggs = json.extract[JsValue]('aggregations)
    AggregationParser.parse(aggs) should be (List(
      JsObject(
        "count" -> JsNumber(3),
        "country" -> JsString("Germany")),
      JsObject(
        "count" -> JsNumber(1),
        "country" -> JsString("United States"))
    ))
  }
}

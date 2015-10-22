package blah.serving

import org.scalatest._
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.AggregationParser
import ServingJsonProtocol._

class ParseCountResponseSpec extends FlatSpec with Matchers {

  val resp = """|{
                |  "aggregations": {
                |    "date": {
                |      "buckets": [{
                |        "key_as_string": "2015-09-05",
                |        "key": 1441411200000,
                |        "doc_count": 100,
                |        "browserFamily": {
                |          "doc_count_error_upper_bound": 0,
                |          "sum_other_doc_count": 0,
                |          "buckets": [{
                |            "key": "Chrome",
                |            "doc_count": 100,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Mac OS X",
                |                "doc_count": 100,
                |                "count": {
                |                  "value": 200
                |                }
                |              }]
                |            }
                |          }]
                |        }
                |      }, {
                |        "key_as_string": "2015-09-06",
                |        "key": 1441497600000,
                |        "doc_count": 100,
                |        "browserFamily": {
                |          "doc_count_error_upper_bound": 0,
                |          "sum_other_doc_count": 0,
                |          "buckets": [{
                |            "key": "Chrome",
                |            "doc_count": 100,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Mac OS X",
                |                "doc_count": 100,
                |                "count": {
                |                  "value": 200
                |                }
                |              }]
                |            }
                |          }]
                |        }
                |      }, {
                |        "key_as_string": "2015-09-07",
                |        "key": 1441584000000,
                |        "doc_count": 100,
                |        "browserFamily": {
                |          "doc_count_error_upper_bound": 0,
                |          "sum_other_doc_count": 0,
                |          "buckets": [{
                |            "key": "Firefox",
                |            "doc_count": 100,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Mac OS X",
                |                "doc_count": 100,
                |                "count": {
                |                  "value": 200
                |                }
                |              }]
                |            }
                |          }]
                |        }
                |      }, {
                |        "key_as_string": "2015-09-08",
                |        "key": 1441670400000,
                |        "doc_count": 502,
                |        "browserFamily": {
                |          "doc_count_error_upper_bound": 0,
                |          "sum_other_doc_count": 0,
                |          "buckets": [{
                |            "key": "Firefox",
                |            "doc_count": 247,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Mac OS X",
                |                "doc_count": 186,
                |                "count": {
                |                  "value": 286
                |                }
                |              }, {
                |                "key": "Windows 2000",
                |                "doc_count": 61,
                |                "count": {
                |                  "value": 161
                |                }
                |              }]
                |            }
                |          }, {
                |            "key": "Chrome",
                |            "doc_count": 132,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Mac OS X",
                |                "doc_count": 132,
                |                "count": {
                |                  "value": 232
                |                }
                |              }]
                |            }
                |          }, {
                |            "key": "Other",
                |            "doc_count": 65,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "Android",
                |                "doc_count": 65,
                |                "count": {
                |                  "value": 165
                |                }
                |              }]
                |            }
                |          }, {
                |            "key": "Chrome Mobile iOS",
                |            "doc_count": 58,
                |            "osFamily": {
                |              "doc_count_error_upper_bound": 0,
                |              "sum_other_doc_count": 0,
                |              "buckets": [{
                |                "key": "iOS",
                |                "doc_count": 58,
                |                "count": {
                |                  "value": 158
                |                }
                |              }]
                |            }
                |          }]
                |        }
                |      }]
                |    }
                |  }
                |}""".stripMargin

  "AggregationParser" should "parse a count response" in {
    val json = resp.parseJson
    val aggs = json.extract[JsValue]('aggregations)
    AggregationParser.parse(aggs) should be (List(
      JsObject(
        "count" -> JsNumber(200),
        "date" -> JsString("2015-09-05"),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(200),
        "date" -> JsString("2015-09-06"),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(200),
        "date" -> JsString("2015-09-07"),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(286),
        "date" -> JsString("2015-09-08"),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(161),
        "date" -> JsString("2015-09-08"),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Windows 2000")),
      JsObject(
        "count" -> JsNumber(232),
        "date" -> JsString("2015-09-08"),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(165),
        "date" -> JsString("2015-09-08"),
        "browserFamily" -> JsString("Other"),
        "osFamily" -> JsString("Android")),
      JsObject(
        "count" -> JsNumber(158),
        "date" -> JsString("2015-09-08"),
        "browserFamily" -> JsString("Chrome Mobile iOS"),
        "osFamily" -> JsString("iOS"))
    ))
  }
}

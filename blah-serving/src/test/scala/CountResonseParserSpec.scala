package blah.serving

import org.scalatest._
import spray.json._
import spray.json.lenses.JsonLenses._
import ServingJsonProtocol._

class CountResponseParserSpec extends FlatSpec with Matchers {

  val resp = """|{
                |  "_shards": {
                |    "total": 5,
                |    "successful": 5,
                |    "failed": 0
                |  },
                |  "timed_out": false,
                |  "took": 8,
                |  "hits": {
                |    "total": 802,
                |    "max_score": 0.0,
                |    "hits": []
                |  },
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
                |                "doc_count": 100
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
                |                "doc_count": 100
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
                |                "doc_count": 100
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
                |                "doc_count": 186
                |              }, {
                |                "key": "Windows 2000",
                |                "doc_count": 61
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
                |                "doc_count": 132
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
                |                "doc_count": 65
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
                |                "doc_count": 58
                |              }]
                |            }
                |          }]
                |        }
                |      }]
                |    }
                |  }
                |}""".stripMargin

  "CountResponseParser" should "parse a es response" in {
    val json = resp.parseJson
    val aggs = json.extract[JsValue]('aggregations)
    val groups = List("date", "browserFamily", "osFamily")
    CountResponseParser.parse(groups, aggs) should be (Vector(
      JsObject(
        "count" -> JsNumber(100),
        "date" -> JsNumber(1441411200000L),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(100),
        "date" -> JsNumber(1441497600000L),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(100),
        "date" -> JsNumber(1441584000000L),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(186),
        "date" -> JsNumber(1441670400000L),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(61),
        "date" -> JsNumber(1441670400000L),
        "browserFamily" -> JsString("Firefox"),
        "osFamily" -> JsString("Windows 2000")),
      JsObject(
        "count" -> JsNumber(132),
        "date" -> JsNumber(1441670400000L),
        "browserFamily" -> JsString("Chrome"),
        "osFamily" -> JsString("Mac OS X")),
      JsObject(
        "count" -> JsNumber(65),
        "date" -> JsNumber(1441670400000L),
        "browserFamily" -> JsString("Other"),
        "osFamily" -> JsString("Android")),
      JsObject(
        "count" -> JsNumber(58),
        "date" -> JsNumber(1441670400000L),
        "browserFamily" -> JsString("Chrome Mobile iOS"),
        "osFamily" -> JsString("iOS"))
    ))
  }
}

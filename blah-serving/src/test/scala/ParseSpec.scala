package blah.serving

import scala.util.Try
import org.scalatest._
import spray.json._
import spray.json.lenses.JsonLenses._
import ServingJsonProtocol._

class ParseSpec extends FlatSpec with Matchers {

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

  "Parse" should "a" in {
    val json = resp.parseJson
    val byDate = json.extract[JsValue]('aggregations)

    val groups = List("date", "browserFamily", "osFamily")
    val res = extract(groups, byDate)

    println(res)
  }

  def extract(
    groups: List[String],
    data: JsValue
  ): Seq[JsObject] = groups match {
    case Nil => Nil
    case x :: Nil => data.extract[JsValue](x / 'buckets / *) map { bucket =>
      val count = bucket.extract[JsValue]('doc_count)
      val key = bucket.extract[JsValue]('key)
      JsObject("count" -> count, x -> key)
    }
    case x :: xs => data.extract[JsValue](x / 'buckets / *) flatMap { bucket =>
      extract(xs, bucket) map {
        val key = bucket.extract[JsValue]('key)
        obj => JsObject(obj.fields ++ Map(x -> key))
      }
    }
  }
}

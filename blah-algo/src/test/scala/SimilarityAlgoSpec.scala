package blah.algo

import org.scalatest._
import com.github.nscala_time.time.Imports._
import spray.json._
import blah.core._
import JsonProtocol._

class SimilarityAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "SimilarityAlgo" should "train" in withSparkContext { sc =>
    val algo = new SimilarityAlgo
    val input = sc.parallelize(List(
      Event("1", "view", DateTime.now, Map(
        "page" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("1", "view", DateTime.now, Map(
        "page" -> JsString("page2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", DateTime.now, Map(
        "page" -> JsString("page3"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", DateTime.now, Map(
        "page" -> JsString("page1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("5", "view", DateTime.now, Map(
        "page" -> JsString("page4"),
        "user" -> JsString("user2")
      )).toJson.compactPrint
    ))

    val output = algo.train(input)
    val docs = output.collect()
    docs foreach {
      case Doc("user1", data) =>
        val views = data("views").asInstanceOf[List[Map[String,Any]]]
        views.length should be (1)
        views(0)("page") should be ("page4")
      case Doc("user2", data) =>
        val views = data("views").asInstanceOf[List[Map[String,Any]]]
        views.length should be (2)
        views(0)("page") should be ("page3")
        views(1)("page") should be ("page2")
    }
  }
}

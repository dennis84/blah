package blah.algo

import org.scalatest._
import com.github.nscala_time.time.Imports._
import spray.json._
import blah.core._
import JsonProtocol._

class SimilarityAlgoSpec extends FlatSpec with SparkFun {

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
    docs collect {
      case Doc("user1", data) =>
        val views = data("views").asInstanceOf[List[Map[String,Any]]]
        assert(views.length == 1)
        assert(views(0)("page") == "page4")
      case Doc("user2", data) =>
        val views = data("views").asInstanceOf[List[Map[String,Any]]]
        assert(views.length == 2)
        assert(views(0)("page") == "page3")
        assert(views(1)("page") == "page2")
    }
  }
}

package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._

class SimilarityAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The SimilarityAlgo" should "train" in withSparkContext { ctx =>
    val algo = new SimilarityAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("1", "view", props = Map(
        "item" -> JsString("page2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page3"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("5", "view", props = Map(
        "item" -> JsString("page4"),
        "user" -> JsString("user2")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList
    docs foreach {
      case ("user1", data) =>
        val views = data.views
        views.length should be (1)
        views(0).item should be ("page4")
      case ("user2", data) =>
        val views = data.views
        views.length should be (2)
        views(0).item should be ("page3")
        views(1).item should be ("page2")
    }
  }

  it should "not fail with wrong data" in withSparkContext { ctx =>
    val algo = new SimilarityAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "foo", props = Map(
        "x" -> JsString("bar")
      )).toJson.compactPrint,
      Event("1", "foo", props = Map(
        "x" -> JsString("baz")
      )).toJson.compactPrint
    ))

    val thrown = intercept[IllegalArgumentException] {
      algo.train(input, ctx, Array.empty[String])
    }

    thrown.getMessage should be
      ("requirement failed: view events cannot be empty")
  }
}

package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._

class RecommendationAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The RecommendationAlgo" should "train" in withSparkContext { ctx =>
    val algo = new RecommendationAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", props = Map(
        "item" -> JsString("page2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("5", "view", props = Map(
        "item" -> JsString("page3"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("6", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("7", "view", props = Map(
        "item" -> JsString("page4"),
        "user" -> JsString("user2")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList

    val user1 = docs.find(_._2.user == "user1").get._2
    val user2 = docs.find(_._2.user == "user2").get._2

    user1.items.length should be (1)
    user1.items(0).item should be ("page4")

    user2.items.length should be (2)
    user2.items(0).item should be ("page3")
    user2.items(1).item should be ("page2")
  }

  it should "filter by collection" in withSparkContext { ctx =>
    val algo = new RecommendationAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "products", props = Map(
        "item" -> JsString("product1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "products", props = Map(
        "item" -> JsString("product2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "products", props = Map(
        "item" -> JsString("product1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("4", "pageviews", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint))

    val output = algo.train(input, ctx, Array("--collection", "products"))
    val docs = output.collect.toList

    val user1 = docs.find(_._2.user == "user1").get._2
    val user2 = docs.find(_._2.user == "user2").get._2

    user1.items.length should be (0)
    user1.collection should be (Some("products"))
    user2.items.length should be (1)
  }

  it should "not fail with wrong data" in withSparkContext { ctx =>
    val algo = new RecommendationAlgo
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

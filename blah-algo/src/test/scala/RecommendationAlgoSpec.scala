package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._

class RecommendationAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The RecommendationAlgo" should "train" in withSparkSession { session =>
    val algo = new RecommendationAlgo
    val input = session.sparkContext.parallelize(List(
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

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList

    val user1 = docs.find(_.user == "user1").get
    val user2 = docs.find(_.user == "user2").get

    user1.items.length should be (1)
    user1.items(0).item should be ("page4")

    user2.items.length should be (2)
    user2.items(0).item should be ("page3")
    user2.items(1).item should be ("page2")
  }

  it should "filter by collection" in withSparkSession { session =>
    val algo = new RecommendationAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "buy", props = Map(
        "item" -> JsString("product1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "buy", props = Map(
        "item" -> JsString("product2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "buy", props = Map(
        "item" -> JsString("product1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("4", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user2")
      )).toJson.compactPrint))

    val output = algo.train(input, session, Array("--collection", "buy"))
    val docs = output.collect.toList

    val user1 = docs.find(_.user == "user1").get
    val user2 = docs.find(_.user == "user2").get

    user1.items.length should be (0)
    user1.collection should be (Some("buy"))
    user2.items.length should be (1)
  }

  it should "not fail with wrong data" in withSparkSession { session =>
    val algo = new RecommendationAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "foo", props = Map(
        "x" -> JsString("bar")
      )).toJson.compactPrint,
      Event("1", "foo", props = Map(
        "x" -> JsString("baz")
      )).toJson.compactPrint
    ))

    val thrown = intercept[IllegalArgumentException] {
      algo.train(input, session, Array.empty[String])
    }

    thrown.getMessage should be
      ("requirement failed: view events cannot be empty")
  }
}

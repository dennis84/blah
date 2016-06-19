package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._

class MostViewedAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The MostViewedAlgo" should "train" in withSparkContext { ctx =>
    val algo = new MostViewedAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("item1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("item1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", props = Map(
        "item" -> JsString("item2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", props = Map(
        "item" -> JsString("item3"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("5", "view", props = Map(
        "item" -> JsString("item4"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("6", "product", props = Map(
        "item" -> JsString("item1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array("--max", "3"))
    val docs = output.collect.toList
    docs.length should be (2)

    val (_, views) = docs.find(_._1 == "view").get
    val (_, products) = docs.find(_._1 == "product").get

    views.items.length should be (3)
    products.items.length should be (1)

    views.items(0).item should be ("item1")
    views.items(0).count should be (2)

    products.items(0).item should be ("item1")
    products.items(0).count should be (1)
  }
}

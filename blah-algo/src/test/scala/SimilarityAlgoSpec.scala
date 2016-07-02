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
        "item" -> JsString("item1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("item2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", props = Map(
        "item" -> JsString("item2"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("4", "view", props = Map(
        "item" -> JsString("item3"),
        "user" -> JsString("user3")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList

    docs.length should be (3)
    val item1 = docs.find(x => x._1 == "item1").get._2
    val item2 = docs.find(x => x._1 == "item2").get._2
    val item3 = docs.find(x => x._1 == "item3").get._2

    item1.similarities.map(_.item) should be (List("item2"))
    item2.similarities.map(_.item) should be (List("item1"))
    item3.similarities.length should be (0)
  }
}

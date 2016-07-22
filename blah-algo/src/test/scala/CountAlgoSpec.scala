package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The CountAlgo" should "train" in withSparkContext { ctx =>
    val algo = new CountAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("3", "buy", props = Map(
        "item" -> JsString("plan-a"),
        "price" -> JsNumber(20.0)
      )).toJson.compactPrint,
      Event("4", "view", ZonedDateTime.now(ZoneOffset.UTC).plusHours(2), Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (3)
  }
}

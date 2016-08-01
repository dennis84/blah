package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class ReferrerAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The ReferrerAlgo" should "train" in withSparkContext { ctx =>
    val algo = new ReferrerAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page2"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("3", "view", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("bing")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (2)

    val google = docs.find(_.url == "google").get
    val bing = docs.find(_.url == "bing").get

    google.count should be (2)
    bing.count should be (1)
  }

  it should "filter by collection" in withSparkContext { ctx =>
    val algo = new ReferrerAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "foo", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("2", "bar", props = Map(
        "item" -> JsString("page2"),
        "referrer" -> JsString("bing")
      )).toJson.compactPrint))

    algo.train(input, ctx, Array.empty[String])
      .collect.toList.length should be (2)

    algo.train(input, ctx, Array("--collection", "foo"))
      .collect.toList.length should be (1)

    algo.train(input, ctx, Array("--collection", "bar"))
      .collect.toList.length should be (1)
  }
}

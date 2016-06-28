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
      Event("1", "pageviews", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("2", "pageviews", props = Map(
        "item" -> JsString("page2"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("3", "pageviews", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("bing")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (2)

    val google = docs.find(_._2.url == "google").get._2
    val bing = docs.find(_._2.url == "bing").get._2

    google.count should be (2)
    bing.count should be (1)
  }
}

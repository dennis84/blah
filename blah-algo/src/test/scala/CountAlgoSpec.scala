package blah.algo

import org.scalatest._
import org.apache.spark.{SparkConf, SparkContext}
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The CountAlgo" should "train" in withSparkContext { sc =>
    val algo = new CountAlgo
    val input = sc.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("3", "view", props = Map(
        "item" -> JsString("page2")
      )).toJson.compactPrint,
      Event("4", "view", ZonedDateTime.now(ZoneOffset.UTC).plusHours(2), Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input)
    val docs = output.collect.toList
    docs.length should be (3)
  }
}

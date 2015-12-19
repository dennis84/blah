package blah.algo

import org.scalatest._
import org.apache.spark.{SparkConf, SparkContext}
import com.github.nscala_time.time.Imports._
import spray.json._
import blah.core._
import JsonProtocol._

class CountAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "CountAlgo" should "train" in withSparkContext { sc =>
    val algo = new CountAlgo
    val input = sc.parallelize(List(
      Event("1", "view", DateTime.now, Map(
        "page" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "view", DateTime.now, Map(
        "page" -> JsString("page1")
      )).toJson.compactPrint,
      Event("3", "view", DateTime.now, Map(
        "page" -> JsString("page2")
      )).toJson.compactPrint,
      Event("4", "view", DateTime.now + 2.hours, Map(
        "page" -> JsString("page1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input)
    val docs = output.collect.toList
    docs.length should be (3)
  }
}

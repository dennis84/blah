package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.testkit._
import blah.core._
import JsonProtocol._

class CountAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The CountAlgo" should "train" in withSparkSession { session =>
    val algo = new CountAlgo
    val input = session.sparkContext.parallelize(List(
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

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (3)
  }

  it should "skip events with invalid property types" in withSparkSession { session =>
    val algo = new CountAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "buy", props = Map(
        "item" -> JsString("foo"),
        "price" -> JsString("10.0")
      )).toJson.compactPrint,
      Event("2", "buy", props = Map(
        "item" -> JsString("bar"),
        "price" -> JsString("10.0")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (0)
  }
}

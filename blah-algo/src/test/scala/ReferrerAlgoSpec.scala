package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.testkit._
import blah.core._
import JsonProtocol._

class ReferrerAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The ReferrerAlgo" should "train" in withSparkSession { session =>
    val algo = new ReferrerAlgo
    val input = session.sparkContext.parallelize(List(
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

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (2)

    val google = docs.find(_.url == "google").get
    val bing = docs.find(_.url == "bing").get

    google.count should be (2)
    bing.count should be (1)
  }

  it should "filter by collection" in withSparkSession { session =>
    val algo = new ReferrerAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "foo", props = Map(
        "item" -> JsString("page1"),
        "referrer" -> JsString("google")
      )).toJson.compactPrint,
      Event("2", "bar", props = Map(
        "item" -> JsString("page2"),
        "referrer" -> JsString("bing")
      )).toJson.compactPrint))

    algo.train(input, session, Array.empty[String])
      .collect.toList.length should be (2)

    algo.train(input, session, Array("--collection", "foo"))
      .collect.toList.length should be (1)

    algo.train(input, session, Array("--collection", "bar"))
      .collect.toList.length should be (1)
  }
}

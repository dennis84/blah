package blah.algo

import org.scalatest._
import spray.json._
import blah.testkit._
import EventJsonProtocol._

class SimilarityAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The SimilarityAlgo" should "train" in withSparkSession { session =>
    val algo = new SimilarityAlgo
    val input = session.sparkContext.parallelize(List(
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

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList

    docs.length should be (3)
    val item1 = docs.find(x => x.item == "item1").get
    val item2 = docs.find(x => x.item == "item2").get
    val item3 = docs.find(x => x.item == "item3").get

    item1.similarities.map(_.item) should be (List("item2"))
    item2.similarities.map(_.item) should be (List("item1"))
    item3.similarities.length should be (0)
  }

  it should "filter by collection" in withSparkSession { session =>
    val algo = new SimilarityAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "foo", props = Map(
        "item" -> JsString("item1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "foo", props = Map(
        "item" -> JsString("item2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "bar", props = Map(
        "item" -> JsString("item3"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "bar", props = Map(
        "item" -> JsString("item4"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("5", "bar", props = Map(
        "item" -> JsString("item5"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    algo.train(input, session, Array.empty[String])
      .collect.toList.length should be (5)

    algo.train(input, session, Array("--collection", "foo"))
      .collect.toList.length should be (2)

    algo.train(input, session, Array("--collection", "bar"))
      .collect.toList.length should be (3)
  }
}

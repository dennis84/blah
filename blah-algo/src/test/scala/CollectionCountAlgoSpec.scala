package blah.algo

import org.scalatest._
import spray.json._
import blah.testkit._
import blah.core._
import JsonProtocol._

class CollectionCountAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The CollectionCountAlgo" should "train" in withSparkSession { session =>
    val algo = new CollectionCountAlgo
    val input = session.sparkContext.parallelize(List(
      Event("1", "foo").toJson.compactPrint,
      Event("2", "foo").toJson.compactPrint,
      Event("3", "bar").toJson.compactPrint
    ))

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (2)
  }
}

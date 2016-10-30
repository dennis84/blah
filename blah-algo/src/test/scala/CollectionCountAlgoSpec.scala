package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.testkit._
import EventJsonProtocol._

class CollectionCountAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The CollectionCountAlgo" should "train" in withSparkSession { session =>
    val algo = new CollectionCountAlgo
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      Event("1", "foo", date = now).toJson.compactPrint,
      Event("2", "foo", date = now).toJson.compactPrint,
      Event("3", "foo", date = now.plusSeconds(1)).toJson.compactPrint,
      Event("4", "bar", date = now).toJson.compactPrint
    ))

    val output = algo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (3)
  }
}

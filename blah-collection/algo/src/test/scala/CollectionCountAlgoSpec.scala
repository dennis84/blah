package blah.collection

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class CollectionCountAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The CollectionCountAlgo" should "train" in withSparkSession { session =>
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      s"""{"id": "1", "collection": "foo", "date": "$now", "props": {}}""",
      s"""{"id": "2", "collection": "foo", "date": "$now", "props": {}}""",
      s"""{"id": "3", "collection": "foo", "date": "${now.plusSeconds(1)}", "props": {}}""",
      s"""{"id": "4", "collection": "bar", "date": "$now", "props": {}}"""
    ))

    val output = CollectionCountAlgo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (3)
  }
}

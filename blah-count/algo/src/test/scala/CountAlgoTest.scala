package blah.count

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class CountAlgoTest extends FlatSpec with Matchers with SparkTest {

  "The CountAlgo" should "train" in withSparkSession { session =>
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      s"""{"id": "1", "collection": "view", "date": "$now", "props": {"item": "page1"}}""",
      s"""{"id": "2", "collection": "view", "date": "$now", "props": {"item": "page1"}}""",
      s"""{"id": "3", "collection": "buy", "date": "$now", "props": {"item": "plan-a", "price": 20}}""",
      s"""{"id": "4", "collection": "view", "date": "${now.plusHours(2)}", "props": {"item": "page1"}}"""
    ))

    val output = CountAlgo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (3)
  }

  it should "skip events with invalid property types" in withSparkSession { session =>
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      s"""{"id": "1", "collection": "buy", "date": "$now", "props": {"item": "foo", "price": "10.0"}}""",
      s"""{"id": "2", "collection": "buy", "date": "$now", "props": {"item": "bar", "price": "10.0"}}"""
    ))

    val output = CountAlgo.train(input, session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (0)
  }
}

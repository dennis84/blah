package blah.recommendation

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class RecommendationAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The RecommendationAlgo" should "train" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "view", """{"item": "page1", "user": "user1"}"""),
      event("2", "view", """{"item": "page1", "user": "user1"}"""),
      event("3", "view", """{"item": "page1", "user": "user1"}"""),
      event("4", "view", """{"item": "page2", "user": "user1"}"""),
      event("5", "view", """{"item": "page3", "user": "user1"}"""),
      event("6", "view", """{"item": "page1", "user": "user2"}"""),
      event("7", "view", """{"item": "page4", "user": "user2"}""")
    ))

    val reader = session.read.schema(RecommendationSchema())
    reader.json(input).createOrReplaceTempView("events")

    val output = RecommendationAlgo.train(session, Array.empty[String])
    val docs = output.collect.toList

    val user1 = docs.find(_.user == "user1").get
    val user2 = docs.find(_.user == "user2").get

    user1.items.length should be (1)
    user1.items(0).item should be ("page4")

    user2.items.length should be (2)
    user2.items(0).item should be ("page3")
    user2.items(1).item should be ("page2")
  }

  it should "filter by collection" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "buy", """{"item": "product1", "user": "user1"}"""),
      event("2", "buy", """{"item": "product2", "user": "user1"}"""),
      event("3", "buy", """{"item": "product1", "user": "user2"}"""),
      event("4", "view", """{"item": "page1", "user": "user2"}""")
    ))

    val reader = session.read.schema(RecommendationSchema())
    reader.json(input).createOrReplaceTempView("events")

    val output = RecommendationAlgo.train(session, Array("--collection", "buy"))
    val docs = output.collect.toList

    val user1 = docs.find(_.user == "user1").get
    val user2 = docs.find(_.user == "user2").get

    user1.items.length should be (0)
    user1.collection should be (Some("buy"))
    user2.items.length should be (1)
  }

  it should "not fail with wrong data" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "foo", """{"x": "bar"}"""),
      event("2", "foo", """{"x": "baz"}""")
    ))

    val reader = session.read.schema(RecommendationSchema())
    reader.json(input).createOrReplaceTempView("events")

    val thrown = intercept[IllegalArgumentException] {
      RecommendationAlgo.train(session, Array.empty[String])
    }

    thrown.getMessage should be
      ("requirement failed: view events cannot be empty")
  }

  def event(id: String, coll: String, props: String) = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    s"""{"id": "$id", "collection": "$coll", "date": "$now", "props": $props}"""
  }
}

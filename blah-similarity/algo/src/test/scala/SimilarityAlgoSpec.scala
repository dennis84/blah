package blah.similarity

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class SimilarityAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The SimilarityAlgo" should "train" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "view", """{"item": "item1", "user": "user1"}"""),
      event("2", "view", """{"item": "item2", "user": "user1"}"""),
      event("3", "view", """{"item": "item2", "user": "user2"}"""),
      event("4", "view", """{"item": "item3", "user": "user3"}""")
    ))

    val output = SimilarityAlgo.train(input, session, Array.empty[String])
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
    val input = session.sparkContext.parallelize(List(
      event("1", "foo", """{"item": "item1", "user": "user1"}"""),
      event("2", "foo", """{"item": "item2", "user": "user1"}"""),
      event("3", "bar", """{"item": "item3", "user": "user1"}"""),
      event("4", "bar", """{"item": "item4", "user": "user1"}"""),
      event("5", "bar", """{"item": "item5", "user": "user1"}""")
    ))

    SimilarityAlgo.train(input, session, Array.empty[String])
      .collect.toList.length should be (5)

    SimilarityAlgo.train(input, session, Array("--collection", "foo"))
      .collect.toList.length should be (2)

    SimilarityAlgo.train(input, session, Array("--collection", "bar"))
      .collect.toList.length should be (3)
  }

  def event(id: String, coll: String, props: String) = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    s"""{"id": "$id", "collection": "$coll", "date": "$now", "props": $props}"""
  }
}

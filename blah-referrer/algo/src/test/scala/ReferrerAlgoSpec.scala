package blah.referrer

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class ReferrerAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The ReferrerAlgo" should "train" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "view", "page1", "google"),
      event("2", "view", "page2", "google"),
      event("3", "view", "page1", "bing")
    ))

    val reader = session.read.schema(ReferrerSchema())
    reader.json(input).createOrReplaceTempView("events")

    val output = ReferrerAlgo.train(session, Array.empty[String])
    val docs = output.collect.toList
    docs.length should be (2)

    val google = docs.find(_.url == "google").get
    val bing = docs.find(_.url == "bing").get

    google.count should be (2)
    bing.count should be (1)
  }

  it should "filter by collection" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      event("1", "foo", "page1", "google"),
      event("1", "bar", "page2", "bing")
    ))

    val reader = session.read.schema(ReferrerSchema())
    reader.json(input).createOrReplaceTempView("events")

    ReferrerAlgo.train(session, Array.empty[String])
      .collect.toList.length should be (2)

    ReferrerAlgo.train(session, Array("--collection", "foo"))
      .collect.toList.length should be (1)

    ReferrerAlgo.train(session, Array("--collection", "bar"))
      .collect.toList.length should be (1)
  }

  def event(id: String, coll: String, item: String, referrer: String) = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val props = s"""{"item": "$item", "referrer": "$referrer"}"""
    s"""{"id": "$id", "collection": "$coll", "date": "$now", "props": $props}"""
  }
}

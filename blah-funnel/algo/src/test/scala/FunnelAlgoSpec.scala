package blah.funnel

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class FunnelAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The FunnelAlgo" should "train" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(
      events("user1", "homepage",            "signup", "signedup") ++
      events("user2", "homepage", "pricing", "signup", "signedup") ++
      events("user3", "homepage", "terms",   "signup"))

    val output = FunnelAlgo.train(input, session, Array(
      "--name", "signup",
      "--steps", "homepage,signup,signedup"))

    val docs = output.collect.toList
    docs.filter(_.item == "homepage").map(_.count).sum should be(3)
    docs.filter(_.item == "signup").map(_.count).sum should be(3)
    docs.filter(_.item == "signedup").map(_.count).sum should be(2)
    docs.filter(_.item == "pricing").map(_.count).sum should be(1)
    docs.filter(_.item == "terms").map(_.count).sum should be(1)
  }

  it should "match no steps" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(
      events("user1", "foo", "bar"))

    val output = FunnelAlgo.train(input, session, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (0)
  }

  it should "remove contiguous duplicates" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(events("user1",
      "landingpage", "signup", "signup", "terms", "signup", "dashboard"))

    val output = FunnelAlgo.train(input, session, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))

    val docs = output.collect.toList
    docs.length should be (5)
    docs.filter(_.item == "signup").map(_.count).sum should be (2)
  }

  it should "parse args" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(
      events("user1", "foo", "bar", "baz"))

    val output = FunnelAlgo.train(input, session, Array(
      "--name", "foobar",
      "--steps", "foo,bar,baz"))
    val docs = output.collect.toList
    docs.length should be (3)
  }

  it should "fail with illegal args" in withSparkSession { session =>
    the [java.lang.IllegalArgumentException] thrownBy {
      FunnelAlgo.train(session.sparkContext.parallelize(Nil), session, Array(
        "--hello", "foobar",
        "--world", "foo,bar,baz"))
    } should have message "Invalid arguments"
  }

  private def events(user: String, items: String*) = {
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    items.zipWithIndex map { case(item, index) =>
      val d = date.plusMinutes(index)
      s"""{"id": "", "collection": "view", "date": "$d", "props": {"item": "$item", "user": "$user"}}"""
    }
  }
}

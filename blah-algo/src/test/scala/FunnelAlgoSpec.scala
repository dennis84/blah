package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.testkit._
import EventJsonProtocol._

class FunnelAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The FunnelAlgo" should "train" in withSparkSession { session =>
    val algo = new FunnelAlgo

    val input = session.sparkContext.parallelize(
      Events("user1", "homepage",            "signup", "signedup") ++
      Events("user2", "homepage", "pricing", "signup", "signedup") ++
      Events("user3", "homepage", "terms",   "signup"))

    val output = algo.train(input, session, Array(
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
    val algo = new FunnelAlgo
    val input = session.sparkContext.parallelize(
      Events("user1", "foo", "bar"))

    val output = algo.train(input, session, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (0)
  }

  it should "remove contiguous duplicates" in withSparkSession { session =>
    val algo = new FunnelAlgo
    val input = session.sparkContext.parallelize(Events("user1",
      "landingpage", "signup", "signup", "terms", "signup", "dashboard"))

    val output = algo.train(input, session, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))

    val docs = output.collect.toList
    docs.length should be (5)
    docs.filter(_.item == "signup").map(_.count).sum should be (2)
  }

  it should "parse args" in withSparkSession { session =>
    val algo = new FunnelAlgo
    val input = session.sparkContext.parallelize(
      Events("user1", "foo", "bar", "baz"))

    val output = algo.train(input, session, Array(
      "--name", "foobar",
      "--steps", "foo,bar,baz"))
    val docs = output.collect.toList
    docs.length should be (3)
  }

  it should "fail with illegal args" in withSparkSession { session =>
    val algo = new FunnelAlgo
    the [java.lang.IllegalArgumentException] thrownBy {
      algo.train(session.sparkContext.parallelize(Nil), session, Array(
        "--hello", "foobar",
        "--world", "foo,bar,baz"))
    } should have message "Invalid arguments"
  }

  private def Events(user: String, items: String*) = {
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    items.zipWithIndex map { case(item, index) =>
      Event("", "view", date.plusMinutes(index), props = Map(
        "item" -> JsString(item),
        "user" -> JsString(user)
      )).toJson.compactPrint
    }
  }
}

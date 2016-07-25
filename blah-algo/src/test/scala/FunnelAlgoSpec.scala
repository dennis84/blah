package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class FunnelAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The FunnelAlgo" should "train" in withSparkContext { ctx =>
    val algo = new FunnelAlgo

    val input = ctx.sparkContext.parallelize(
      Events("user1", "homepage",            "signup", "signedup") ++
      Events("user2", "homepage", "pricing", "signup", "signedup") ++
      Events("user3", "homepage", "terms",   "signup"))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "homepage,signup,signedup"))

    val docs = output.collect.toList
    docs.filter(_._2.item == "homepage").map(_._2.count).sum should be(3)
    docs.filter(_._2.item == "signup").map(_._2.count).sum should be(3)
    docs.filter(_._2.item == "signedup").map(_._2.count).sum should be(2)
    docs.filter(_._2.item == "pricing").map(_._2.count).sum should be(1)
    docs.filter(_._2.item == "terms").map(_._2.count).sum should be(1)
  }

  it should "match no steps" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(
      Events("user1", "foo", "bar"))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (0)
  }

  it should "remove contiguous duplicates" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(Events("user1",
      "landingpage", "signup", "signup", "terms", "signup", "dashboard"))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))

    val docs = output.collect.toList
    docs.length should be (5)
    docs.filter(_._2.item == "signup").map(_._2.count).sum should be (2)
  }

  it should "parse args" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(
      Events("user1", "foo", "bar", "baz"))

    val output = algo.train(input, ctx, Array(
      "--name", "foobar",
      "--steps", "foo,bar,baz"))
    val docs = output.collect.toList
    docs.length should be (3)
  }

  it should "fail with illegal args" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    the [java.lang.IllegalArgumentException] thrownBy {
      algo.train(ctx.sparkContext.parallelize(Nil), ctx, Array(
        "--hello", "foobar",
        "--world", "foo,bar,baz"))
    } should have message "Invalid arguments"
  }

  def Events(user: String, items: String*) = {
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    items.zipWithIndex map { case(item, index) =>
      Event("", "view", date.plusMinutes(index), props = Map(
        "item" -> JsString(item),
        "user" -> JsString(user)
      )).toJson.compactPrint
    }
  }
}

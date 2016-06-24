package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class FunnelAlgoSpec extends FlatSpec with Matchers with Inside with SparkFun {

  val date1 = ZonedDateTime.now(ZoneOffset.UTC)
  val date2 = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(1)
  val date3 = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(2)
  val date4 = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(3)
  val date5 = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(4)

  "The FunnelAlgo" should "count all step" in withSparkContext { ctx =>
    val algo = new FunnelAlgo

    val input = ctx.sparkContext.parallelize(List(
      Event("2", "view", date2, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", date3, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", date4, props = Map(
        "item" -> JsString("dashboard"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("6", "view", date1, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("7", "view", date2, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("6", "view", date1, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user3")
      )).toJson.compactPrint,
      Event("8", "view", date1, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user4")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (3)

    val funnel3 = docs.find(_._2.path.length == 3).get._2
    funnel3.count should be (1)
    val funnel2 = docs.find(_._2.path.length == 2).get._2
    funnel2.count should be (2)
    val funnel1 = docs.find(_._2.path.length == 1).get._2
    funnel1.count should be (3)
  }

  it should "match all steps" in withSparkContext { ctx =>
    val algo = new FunnelAlgo

    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", date1, props = Map(
        "item" -> JsString("home"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date2, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", date3, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", date4, props = Map(
        "item" -> JsString("dashboard"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("5", "view", date5, props = Map(
        "item" -> JsString("settings"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,

      Event("6", "view", date1, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("7", "view", date2, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user2")
      )).toJson.compactPrint,
      Event("8", "view", date3, props = Map(
        "item" -> JsString("dashboard"),
        "user" -> JsString("user2")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (3)

    val funnel3 = docs.find(_._2.path.length == 3).get._2
    funnel3.count should be (2)
  }

  it should "match two steps" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", date2, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date3, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", date4, props = Map(
        "item" -> JsString("terms"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (2)

    val funnel2 = docs.find(_._2.path.length == 2).get._2
    funnel2.count should be (1)
    val funnel1 = docs.find(_._2.path.length == 1).get._2
    funnel1.count should be (1)
  }

  it should "match no steps" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", date1, props = Map(
        "item" -> JsString("foo"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date2, props = Map(
        "item" -> JsString("bar"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (0)
  }

  it should "remove contiguous duplicates" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", date1, props = Map(
        "item" -> JsString("landingpage"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date2, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", date2, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("4", "view", date2, props = Map(
        "item" -> JsString("signup"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("5", "view", date2, props = Map(
        "item" -> JsString("dashboard"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (3)

    val funnel3 = docs.find(_._2.path.length == 3).get._2
    funnel3.count should be (1)
  }

  it should "parse args" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "view", date1, props = Map(
        "item" -> JsString("foo"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date2, props = Map(
        "item" -> JsString("bar"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("3", "view", date3, props = Map(
        "item" -> JsString("baz"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, ctx, Array(
      "--name", "foobar",
      "--steps", "foo,bar,baz"))
    val docs = output.collect.toList
    docs.length should be (3)

    val funnel3 = docs.find(_._2.path.length == 3).get._2
    funnel3.count should be (1)
    val funnel2 = docs.find(_._2.path.length == 2).get._2
    funnel2.count should be (1)
    val funnel1 = docs.find(_._2.path.length == 1).get._2
    funnel1.count should be (1)
  }

  it should "fail with illegal args" in withSparkContext { ctx =>
    val algo = new FunnelAlgo
    the [java.lang.IllegalArgumentException] thrownBy {
      algo.train(ctx.sparkContext.parallelize(Nil), ctx, Array(
        "--hello", "foobar",
        "--world", "foo,bar,baz"))
    } should have message "Invalid arguments"
  }
}

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

  "The FunnelAlgo" should "match all steps" in withSparkContext { sc =>
    val algo = new FunnelAlgo

    val input = sc.parallelize(List(
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

    val output = algo.train(input, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList

    inside(docs) { case Doc(_, map) :: Nil =>
      map should be (Map(
        "name" -> "signup",
        "path" -> List("landingpage", "signup", "dashboard"),
        "count" -> 2
      ))
    }
  }

  it should "match two steps" in withSparkContext { sc =>
    val algo = new FunnelAlgo
    val input = sc.parallelize(List(
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

    val output = algo.train(input, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList

    inside(docs) { case Doc(_, map) :: Nil =>
      map should be (Map(
        "name" -> "signup",
        "path" -> List("landingpage", "signup"),
        "count" -> 1
      ))
    }
  }

  it should "match no steps" in withSparkContext { sc =>
    val algo = new FunnelAlgo
    val input = sc.parallelize(List(
      Event("1", "view", date1, props = Map(
        "item" -> JsString("foo"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date2, props = Map(
        "item" -> JsString("bar"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList
    docs.length should be (0)
  }

  it should "remove contiguous duplicates" in withSparkContext { sc =>
    val algo = new FunnelAlgo
    val input = sc.parallelize(List(
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

    val output = algo.train(input, Array(
      "--name", "signup",
      "--steps", "landingpage,signup,dashboard"))
    val docs = output.collect.toList

    inside(docs) { case Doc(_, map) :: Nil =>
      map should be (Map(
        "name" -> "signup",
        "path" -> List("landingpage", "signup", "dashboard"),
        "count" -> 1
      ))
    }
  }

  it should "parse args" in withSparkContext { sc =>
    val algo = new FunnelAlgo
    val input = sc.parallelize(List(
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

    val output = algo.train(input, Array(
      "--name", "foobar",
      "--steps", "foo,bar,baz"))
    val docs = output.collect.toList

    inside(docs) { case Doc(_, map) :: Nil =>
      map should be (Map(
        "name" -> "foobar",
        "path" -> List("foo", "bar", "baz"),
        "count" -> 1
      ))
    }
  }

  it should "fail with illegal args" in withSparkContext { sc =>
    val algo = new FunnelAlgo
    the [java.lang.IllegalArgumentException] thrownBy {
      algo.train(sc.parallelize(Nil), Array(
        "--hello", "foobar",
        "--world", "foo,bar,baz"))
    } should have message "Invalid arguments"
  }
}

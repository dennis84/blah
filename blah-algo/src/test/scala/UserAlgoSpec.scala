package blah.algo

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The UserAlgo" should "train" in withSparkSession { session =>
    import session.implicits._
    val algo = new UserAlgo
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      Event("1", "view", date.plusMinutes(0), props = Map(
        "item" -> JsString("page1"),
        "title" -> JsString("title1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", date.plusMinutes(1), props = Map(
        "item" -> JsString("page2"),
        "title" -> JsString("title2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, session, Array.empty[String])
    val users = output.collect.toList
    users.length should be (1)
    val events = users(0).events
    events(0).title should be (Some("title2"))
    events(1).title should be (Some("title1"))
  }

  it should "update users" in withSparkSession { session =>
    val algo = new UserAlgo
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      Event("1", "view", date.plusMinutes(0), props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("test")
      )).toJson.compactPrint,
      Event("2", "signup", date.plusMinutes(1), props = Map(
        "email" -> JsString("test@example.com"),
        "firstname" -> JsString("foo"),
        "lastname" -> JsString("bar"),
        "user" -> JsString("test"),
        "ip" -> JsString("8.8.8.8")
      )).toJson.compactPrint,
      Event("3", "update", date.plusMinutes(2), props = Map(
        "email" -> JsString("foo@example.com"),
        "firstname" -> JsString("blah"),
        "lastname" -> JsString("blub"),
        "user" -> JsString("test"),
        "ip" -> JsString("8.8.8.8")
      )).toJson.compactPrint,
      Event("4", "update", date.plusMinutes(3), props = Map(
        "firstname" -> JsString("baz"),
        "user" -> JsString("test")
      )).toJson.compactPrint,
      Event("5", "view", date.plusMinutes(4), props = Map(
        "item" -> JsString("page2"),
        "user" -> JsString("test")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, session, Array.empty[String])
    val users = output.collect.toList

    users.length should be (1)
    val user = users(0)

    user.email should be (Some("foo@example.com"))
    user.firstname should be (Some("baz"))
    user.lastname should be (Some("blub"))
  }
}

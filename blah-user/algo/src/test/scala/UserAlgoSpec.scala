package blah.user

import org.scalatest._
import java.time.{ZonedDateTime, ZoneOffset}

class UserAlgoSpec extends FlatSpec with Matchers with SparkTest {

  "The UserAlgo" should "train" in withSparkSession { session =>
    import session.implicits._
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      event("1", "view", date, """{
        "item": "page1",
        "title": "title1",
        "user": "user1"
      }"""),
      event("2", "view", date.plusMinutes(1), """{
        "item": "page2",
        "title": "title2",
        "user": "user1"
      }""")
    ))

    val output = UserAlgo.train(input, session, Array.empty[String])
    val users = output.collect.toList
    users.length should be (1)
    val events = users(0).events
    events(0).title should be (Some("title2"))
    events(1).title should be (Some("title1"))
  }

  it should "update users" in withSparkSession { session =>
    val date = ZonedDateTime.now(ZoneOffset.UTC)
    val input = session.sparkContext.parallelize(List(
      event("1", "view", date, """{
        "item": "page1",
        "user": "test"
      }"""),
      event("2", "signup", date.plusMinutes(1), """{
        "email": "test@example.com",
        "firstname": "foo",
        "lastname": "bar",
        "user": "test",
        "ip": "8.8.8.8"
      }"""),
      event("3", "update", date.plusMinutes(2), """{
        "email": "foo@example.com",
        "firstname": "blah",
        "lastname": "blub",
        "user": "test",
        "ip": "8.8.8.8"
      }"""),
      event("4", "update", date.plusMinutes(3), """{
        "firstname": "baz",
        "user": "test"
      }"""),
      event("5", "view", date.plusMinutes(4), """{
        "item": "page2",
        "user": "test"
      }""")
    ))

    val output = UserAlgo.train(input, session, Array.empty[String])
    val users = output.collect.toList

    users.length should be (1)
    val user = users(0)

    user.email should be (Some("foo@example.com"))
    user.firstname should be (Some("baz"))
    user.lastname should be (Some("blub"))
  }

  def event(id: String, coll: String, date: ZonedDateTime, props: String) = {
    s"""{"id": "$id", "collection": "$coll", "date": "$date", "props": $props}"""
  }
}

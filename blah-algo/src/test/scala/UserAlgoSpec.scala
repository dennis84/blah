package blah.algo

import org.scalatest._
import com.github.nscala_time.time.Imports._
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "UserAlgo" should "train" in withSparkContext { sc =>
    val algo = new UserAlgo
    val input = sc.parallelize(List(
      Event("1", "view", DateTime.now, Map(
        "page" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("1", "view", DateTime.now, Map(
        "page" -> JsString("page2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input)
    val users = output.collect.toList
    assert(users.length == 1)
  }
}

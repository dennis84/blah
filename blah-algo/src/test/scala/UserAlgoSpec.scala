package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._

class UserAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The UserAlgo" should "train" in withSparkContext { sc =>
    val algo = new UserAlgo
    val input = sc.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("1", "view", props = Map(
        "item" -> JsString("page2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input)
    val users = output.collect.toList
    users.length should be (1)
  }
}

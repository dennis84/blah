package blah.algo

import org.scalatest._
import spray.json._
import blah.core._
import JsonProtocol._
import org.apache.spark.sql.types.{StructType,StructField,StringType}

class UserAlgoSpec extends FlatSpec with Matchers with SparkFun {

  "The UserAlgo" should "train" in withSparkSqlContext { (sc, sqlContext) =>
    val algo = new UserAlgo
    val input = sc.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1"),
        "title" -> JsString("title1"),
        "user" -> JsString("user1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page2"),
        "title" -> JsString("title2"),
        "user" -> JsString("user1")
      )).toJson.compactPrint
    ))

    val output = algo.train(input, sqlContext, Array.empty[String])
    val users = output.collect.toList.asInstanceOf[List[(String, User)]]
    users.length should be (1)
    val events = users(0)._2.events
    events(0).title should be (Some("title2"))
    events(1).title should be (Some("title1"))
  }
}

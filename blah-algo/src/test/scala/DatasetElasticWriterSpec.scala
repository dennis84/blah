package blah.algo

import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import org.apache.spark.SparkConf
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.testkit._
import blah.core._
import blah.core.JsonDsl._
import JsonProtocol._
import DatasetElasticWriter._

case class Person(
  id: Option[String],
  firstname: String,
  lastname: String,
  address: Option[String] = None)

class DatasetElasticWriterSpec
  extends FlatSpec
  with Matchers
  with SparkTest
  with SprayJsonSupport {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val conf = new SparkConf()
    .setMaster("local")
    .setAppName(this.getClass.getName)
  conf.set("elastic.url", "http://localhost:9200")

  val scriptConf = new SparkConf()
    .setMaster("local")
    .setAppName(this.getClass.getName)
  scriptConf.set("elastic.url", "http://localhost:9200")
  scriptConf.set("elastic.script",
    """|ctx._source.firstname = params.lastname;
       |ctx._source.lastname = params.firstname;
       |if(params.address) ctx._source.address = 'c'
       |""".stripMargin.replaceAll("\n", ""))

  "A DatasetElasticWriter" should "insert" in withSparkSession(conf) { session =>
    assume(isReachable("localhost", 9200))
    Await.ready(delete, 10.seconds)

    import session.implicits._
    val input = session.sparkContext.parallelize(List(
      Person(None, "foo", "bar"),
      Person(None, "baz", "qux")
    ))

    input.toDS.writeToElastic("test", "person")
    Thread.sleep(1000)

    val res = Await.result(search, 10.seconds)

    res should contain theSameElementsAs List(
      ("firstname" -> "baz") ~ ("lastname" -> "qux"),
      ("firstname" -> "foo") ~ ("lastname" -> "bar")
    )
  }

  it should "upsert" in withSparkSession(conf) { session =>
    assume(isReachable("localhost", 9200))
    Await.ready(delete, 10.seconds)

    import session.implicits._
    val input = session.sparkContext.parallelize(List(
      Person(Some("1"), "foo", "bar"),
      Person(Some("2"), "baz", "qux")
    ))

    input.toDS.writeToElastic("test", "person")
    Thread.sleep(1000)

    val res = Await.result(search, 10.seconds)

    res should contain theSameElementsAs List(
      ("firstname" -> "baz") ~ ("lastname" -> "qux"),
      ("firstname" -> "foo") ~ ("lastname" -> "bar")
    )
  }

  it should "upsert with script" in withSparkSession(scriptConf) { session =>
    assume(isReachable("localhost", 9200))
    Await.ready(delete, 10.seconds)

    import session.implicits._
    val input = session.sparkContext.parallelize(List(
      Person(Some("1"), "a", "b"),
      Person(Some("2"), "c", "d")
    ))

    input.toDS.writeToElastic("test", "person")
    Thread.sleep(1000)

    val res1 = Await.result(search, 10.seconds)

    res1 should contain theSameElementsAs List(
      ("firstname" -> "a") ~ ("lastname" -> "b"),
      ("firstname" -> "c") ~ ("lastname" -> "d")
    )

    input.toDS.writeToElastic("test", "person")
    Thread.sleep(1000)

    val res2 = Await.result(search, 10.seconds)

    res2 should contain theSameElementsAs List(
      ("firstname" -> "b") ~ ("lastname" -> "a"),
      ("firstname" -> "d") ~ ("lastname" -> "c")
    )

    val withAddress = session.sparkContext.parallelize(List(
      Person(Some("1"), "a", "b", Some("x")),
      Person(Some("2"), "c", "d", Some("x"))
    ))

    withAddress.toDS.writeToElastic("test", "person")
    Thread.sleep(1000)

    val res3 = Await.result(search, 10.seconds)

    res3 should contain theSameElementsAs List(
      ("firstname" -> "b") ~ ("lastname" -> "a") ~ ("address" -> "c"),
      ("firstname" -> "d") ~ ("lastname" -> "c") ~ ("address" -> "c")
    )
  }

  private def delete() =
    Http() singleRequest HttpRequest(
      method = HttpMethods.DELETE,
      uri = "http://localhost:9200/test")

  private def search() =
    Http() singleRequest HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/test/person/_search?q=*:*"
    ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      json.extract[JsObject]('hits / 'hits / * / '_source).toList
    }
}

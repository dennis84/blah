package blah.algo

import org.scalatest._
import org.apache.spark.SparkConf
import spray.json._
import blah.testkit._
import blah.core._
import JsonProtocol._
import DatasetElasticWriter._

case class Person(id: String, name: String)

class DatasetElasticWriterSpec extends FlatSpec with Matchers with SparkTest {

  val conf = new SparkConf()
    .setMaster("local")
    .setAppName(this.getClass.getName)
  conf.set("elastic.url", "http://localhost:9200")

  "A DatasetElasticWriter" should "write" in withSparkSession(conf) { session =>
    assume(isReachable("localhost", 9200))
    import session.implicits._
    val input = session.sparkContext.parallelize(List(
      Person("1", "foo"),
      Person("2", "bar")))

    input.toDS.writeToElastic()
  }

  // it should "write" in withSparkSession(conf) { session =>
  //   assume(isReachable("localhost", 9200))
  //   //conf.set("elastic.script", "ctx._source.name = name")
  //   import session.implicits._
  //   val input = session.sparkContext.parallelize(List(
  //     Person("1", "foo"),
  //     Person("2", "bar")))

  //   input.toDS.writeToElastic()
  // }
}

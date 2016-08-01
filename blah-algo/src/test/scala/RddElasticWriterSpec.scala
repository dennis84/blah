package blah.algo

import org.scalatest._
import org.elasticsearch.spark.sql._

case class Foo(x: String, y: Int)

@Ignore
class RddElasticWriterSpec extends FlatSpec with Matchers with SparkFun {

  "The RddKafkaWriter" should "write to elasticsearch" in withSparkContext { ctx =>
    import ctx.implicits._

    val input = ctx.createDataset(List(
      Foo("one", 1),
      Foo("two", 2)))

    //input.foreach(x => println(x))
    input
      .saveToEs("test/foos", Map(
        "es.nodes" -> "localhost:9200",
        "es.index.auto.create" -> "true",
        "es.mapping.id" -> "x",
        "es.mapping.exclude" -> "x"
      ))
  }
}

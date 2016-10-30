package blah.algo

import java.util.Properties
import org.scalatest._
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json._
import blah.testkit._
import DatasetKafkaWriter._
import EventJsonProtocol._

/**
 * Start kafka:
 *
 * ```bash
 * cd path/to/kafka
 * bin/zookeeper-server-start.sh config/zookeeper.properties
 * bin/kafka-server-start.sh config/server.properties
 * bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test
 * ```
 */
class DatasetKafkaWriterSpec extends FlatSpec with Matchers with SparkTest {

  val props = new Properties
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  "The RddKafkaWriter" should "write to kafka" in withSparkSession { session =>
    assume(isReachable("localhost", 9092))
    import session.implicits._
    val input = session.sparkContext.parallelize(List("foo", "bar", "baz"))
    input.toDS.writeToKafka(props, x =>
      new ProducerRecord[String, String]("test", x))
  }

  it should "write json" in withSparkSession { session =>
    assume(isReachable("localhost", 9092))
    val input = session.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page2")
      )).toJson.compactPrint))

    val df = session.read.json(input)
    df.toJSON.writeToKafka(props, x =>
      new ProducerRecord[String, String]("test", x))
  }
}

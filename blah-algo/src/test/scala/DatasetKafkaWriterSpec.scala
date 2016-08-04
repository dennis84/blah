package blah.algo

import java.util.Properties
import org.scalatest._
import kafka.producer.KeyedMessage
import spray.json._
import blah.core._
import JsonProtocol._
import DatasetKafkaWriter._

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
@Ignore
class DatasetKafkaWriterSpec extends FlatSpec with Matchers with SparkTest {

  val props = new Properties
  props.put("metadata.broker.list", "localhost:9092")
  props.put("serializer.class", "kafka.serializer.DefaultEncoder")
  props.put("key.serializer.class", "kafka.serializer.StringEncoder")

  "The RddKafkaWriter" should "write to kafka" in withSparkSession { session =>
    import session.implicits._
    val input = session.sparkContext.parallelize(List("foo", "bar", "baz"))
    input.toDS.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }

  it should "write json" in withSparkSession { session =>
    val input = session.sparkContext.parallelize(List(
      Event("1", "view", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "view", props = Map(
        "item" -> JsString("page2")
      )).toJson.compactPrint))

    val df = session.read.json(input)
    df.toJSON.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }
}

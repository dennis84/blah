package blah.algo

import java.util.Properties
import org.scalatest._
import kafka.producer.KeyedMessage
import spray.json._
import blah.core._
import JsonProtocol._
import RddKafkaWriter._

object KafkaTest extends Tag("blah.algo.KafkaTest")

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
class RddKafkaWriterSpec extends FlatSpec with Matchers with SparkFun {

  val props = new Properties
  props.put("metadata.broker.list", "localhost:9092")
  props.put("serializer.class", "kafka.serializer.DefaultEncoder")
  props.put("key.serializer.class", "kafka.serializer.StringEncoder")

  "The RddKafkaWriter" should "write to kafka" taggedAs(KafkaTest) in withSparkContext { (sc, _) =>
    val input = sc.parallelize(List("foo", "bar", "baz"))
    input.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }

  it should "write json" taggedAs(KafkaTest) in withSparkContext { (sc, sqlContext) =>
    val input = sc.parallelize(List(
      Event("1", "pageviews", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "pageviews", props = Map(
        "item" -> JsString("page2")
      )).toJson.compactPrint))

    val df = sqlContext.read.json(input)
    df.toJSON.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }
}

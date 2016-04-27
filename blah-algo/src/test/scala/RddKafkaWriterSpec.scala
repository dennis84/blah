package blah.algo

import java.util.Properties
import org.scalatest._
import kafka.producer.KeyedMessage
import spray.json._
import blah.core._
import JsonProtocol._
import RddKafkaWriter._

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
class RddKafkaWriterSpec extends FlatSpec with Matchers with SparkFun {

  val props = new Properties
  props.put("metadata.broker.list", "localhost:9092")
  props.put("serializer.class", "kafka.serializer.DefaultEncoder")
  props.put("key.serializer.class", "kafka.serializer.StringEncoder")

  "The RddKafkaWriter" should "write to kafka" in withSparkContext { ctx =>
    val input = ctx.sparkContext.parallelize(List("foo", "bar", "baz"))
    input.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }

  it should "write json" in withSparkContext { ctx =>
    val input = ctx.sparkContext.parallelize(List(
      Event("1", "pageviews", props = Map(
        "item" -> JsString("page1")
      )).toJson.compactPrint,
      Event("2", "pageviews", props = Map(
        "item" -> JsString("page2")
      )).toJson.compactPrint))

    val df = ctx.read.json(input)
    df.toJSON.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("test", null, x.getBytes))
  }
}

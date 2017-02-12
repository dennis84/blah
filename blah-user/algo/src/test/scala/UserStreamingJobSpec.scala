package blah.user

import org.scalatest._
import java.io.IOException
import java.net.{InetSocketAddress, Socket}
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

/**
 * ```
 * cd path/to/kafka
 * bin/zookeeper-server-start.sh config/zookeeper.properties
 * bin/kafka-server-start.sh config/server.properties
 * bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic events
 * bin/kafka-console-producer.sh --broker-list localhost:9092 --topic events
 *
 * {"id":"1","collection":"test","date":"2016-08-02T18:21:12.946Z","props":{"item":"landingpage","user": "dennis"}}
 * ```
 */
class UserStreamingJobSpec extends FlatSpec with Matchers {

  "The UserStreamingJob" should "run" in {
    assume(isReachable("localhost", 9200))
    assume(isReachable("localhost", 9092))

    val sparkConf = new SparkConf()
      .setMaster("local")
      .setAppName("user streaming job test")
    sparkConf.set("elastic.url", "http://localhost:9200")

    val conf = ConfigFactory.parseMap(Map(
      "streaming.batch.interval" -> 10,
      "consumer.broker.list" -> "localhost:9092",
      "producer.broker.list" -> "localhost:9092"
    ))

    StreamingJob.run(conf, sparkConf, Array.empty[String])
  }

  def isReachable(
    host: String,
    port: Int,
    timeout: Int = 2000
  ): Boolean = {
    val socket = new Socket
    try {
      socket.connect(new InetSocketAddress(host, port), timeout)
      true
    } catch {
      case e: IOException => false
    } finally {
      socket.close()
    }
  }
}

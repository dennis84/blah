package blah.example

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.serializer.StringDecoder
import com.datastax.spark.connector.streaming._
import spray.json._
import blah.core._

object Worker extends App with JsonProtocol with SetUp {
  lazy val cluster = DefaultCassandraCluster()
  lazy val conn = cluster.connect("blah")

  withSchema(cluster) {
    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("example")
      .set("spark.cassandra.connection.host", "127.0.0.1")
      .set("spark.cleaner.ttl", "5000")
    val ssc = new StreamingContext(conf, Seconds(1))

    val stream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
      ssc = ssc,
      kafkaParams = Map(
        "group.id" -> "1234",
        "zookeeper.connect" -> "localhost:2181",
        "auto.offset.reset" -> "smallest"),
      topics = Map("foo" -> 1),
      storageLevel = StorageLevel.MEMORY_ONLY
    ).map(_._2)

    val events = stream
      .map(_.parseJson.convertTo[Event])
      .map(x => (x.name, 1))
      .reduceByKey(_ + _)

    events.saveToCassandra("blah", "example")
    events.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

package blah.example

import kafka.serializer.StringDecoder
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector._
import spray.json._
import blah.core._

object Worker extends App with JsonProtocol with SetUp {
  import org.apache.log4j.Logger
  import org.apache.log4j.Level

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
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
      topics = Map("events_1" -> 1),
      storageLevel = StorageLevel.MEMORY_ONLY
    ).map(_._2)

    val events = stream
      .map(_.parseJson.convertTo[Event])
      .map(x => (x.name, x.date, 1))
      .reduce((a, b) => (a._1, a._2, a._3 + b._3))

    events.saveToCassandra("blah", "example", SomeColumns("name", "date", "count"))
    events.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

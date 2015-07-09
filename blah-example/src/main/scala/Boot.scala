package blah.example

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.serializer.StringDecoder
import spray.json._
import blah.core._

object Boot extends App with Protocols {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val conf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("example")
    .set("spark.executor.memory", "1g")
  val ssc = new StreamingContext(conf, Seconds(2))

  val stream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
    ssc = ssc,
    kafkaParams = Map(
      "group.id" -> "1234",
      "zookeeper.connect" -> "localhost:2181",
      "auto.offset.reset" -> "smallest"
    ),
    Map("foo" -> 1),
    StorageLevel.MEMORY_ONLY
  ).map(_._2)

  val events = stream.map(_.parseJson.convertTo[Event])
  val xs = events.map(x => (x.name, 1)).reduceByKey(_ + _)

  xs.print()

  ssc.start()
  ssc.awaitTermination()
}

package blah.streaming

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import com.typesafe.config.ConfigFactory
import blah.algo._

object StreamingApp {
  def main(args: Array[String]) {
    val algos = Map(
      "count" -> new CountAlgo)
    val algo = algos(args(0))
    val config = ConfigFactory.load()
    val conf = new SparkConf()
      .setAppName(s"streaming-${args(0)}")
    conf.set("es.nodes", config.getString("elasticsearch.url"))
    conf.set("es.index.auto.create", "false")
    conf.set("es.write.operation", "upsert")
    conf.set("es.update.script", "ctx._source.count += count")
    conf.set("es.update.script.params", "count:count")

    val ssc = new StreamingContext(conf, Seconds(10))
    val stream = KafkaUtils.createStream(
      ssc,
      config.getString("consumer.zookeeper.connect"),
      config.getString("consumer.group.id"),
      Map("events" -> 1)
    ).map(_._2)

    stream.foreachRDD(algo.train _)
    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

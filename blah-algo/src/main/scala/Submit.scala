package blah.algo

import org.apache.spark.{SparkConf, SparkContext}
import kafka.serializer.StringEncoder
import kafka.producer.KafkaProducer
import com.softwaremill.react.kafka.ProducerProperties
import com.typesafe.config.ConfigFactory

object Submit {
  def main(args: Array[String]) {
    val config = ConfigFactory.load()
    val producer = KafkaProducer[String](ProducerProperties(
      brokerList = config.getString("producer.broker.list"),
      topic = "trainings",
      clientId = "websocket",
      encoder = new StringEncoder()
    ))

    val algos = Map(
      "count" -> new CountAlgo,
      "similarity" -> new SimilarityAlgo)
    val algo = algos(args(0))
    val conf = new SparkConf()
      .setAppName(args(0))
    conf.set("es.nodes", config.getString("elasticsearch.url"))
    conf.set("es.index.auto.create", "false")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(config.getString("hadoop.url") + "/blah/events.*.jsonl")
    algo.train(rdd)
    producer.send(args(0))
    sc.stop
  }
}

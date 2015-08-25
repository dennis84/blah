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
      .set("spark.cassandra.connection.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("hdfs://localhost:9000/blah/events.*")
    algo.train(rdd)
    producer.send(args(0))
    sc.stop
  }
}

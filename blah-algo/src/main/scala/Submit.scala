package blah.algo

import scala.util.{Try, Success, Failure}
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
      encoder = new StringEncoder
    ))

    val algos = Map(
      "count" -> new CountAlgo,
      "similarity" -> new SimilarityAlgo,
      "user" -> new UserAlgo)
    val algo = algos(args(0))
    val path = args.lift(1).getOrElse("*/*/*")
    val conf = new SparkConf()
      .setAppName(args(0))
    conf.set("es.nodes", config.getString("elasticsearch.url"))
    conf.set("es.index.auto.create", "false")
    val hadoopUrl = config.getString("hadoop.url")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(s"$hadoopUrl/events/$path/*.jsonl")
    algo.train(rdd)

    Try(producer send args(0)) match {
      case Success(_) => println("Successfully sent message")
      case Failure(e) => println("Message could not be sent")
    }

    sc.stop
  }
}

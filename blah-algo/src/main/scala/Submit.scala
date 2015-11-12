package blah.algo

import kafka.serializer.StringEncoder
import kafka.producer.KafkaProducer
import org.apache.spark.SparkConf
import com.softwaremill.react.kafka.ProducerProperties
import com.typesafe.config.ConfigFactory

object Submit {
  def main(args: Array[String]) {
    val config = ConfigFactory.load()
    val producer = KafkaProducer[String](ProducerProperties(
      brokerList = config.getString("producer.broker.list"),
      topic = "trainings",
      clientId = "websocket",
      encoder = new StringEncoder))

    lazy val countAlgo = new CountAlgo
    lazy val similarityAlgo = new SimilarityAlgo
    lazy val userAlgo = new UserAlgo

    lazy val countBatch = new BatchJob(config, countAlgo, producer, "count")
    lazy val similarityBatch = new BatchJob(config, similarityAlgo, producer, "similarity")
    lazy val userBatch = new BatchJob(config, userAlgo, producer, "user")
    lazy val countStream = new CountStreamingJob(config, countAlgo, producer, "count")
    lazy val userStream = new StreamingJob(config, userAlgo, producer, "user")
    
    val conf = new SparkConf().setAppName(args(0))
    conf.set("es.nodes", config.getString("elasticsearch.url"))
    conf.set("es.index.auto.create", "false")

    val jobs = Map(
      "count" -> countBatch,
      "similarity" -> similarityBatch,
      "user" -> userBatch,
      "count-stream" -> countStream,
      "user-stream" -> userStream)

    for {
      algo <- args lift 0
      job <- jobs get algo
    } yield job.run(conf, args)
  }
}

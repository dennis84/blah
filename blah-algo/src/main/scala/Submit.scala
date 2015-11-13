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

    lazy val countBatch = new BatchJob(countAlgo, producer, "count")
    lazy val similarityBatch = new BatchJob(similarityAlgo, producer, "similarity")
    lazy val userBatch = new BatchJob(userAlgo, producer, "user")
    lazy val countStreaming = new CountStreamingJob(countAlgo, producer, "count")
    lazy val userStreaming = new StreamingJob(userAlgo, producer, "user")
    
    val sparkConf = new SparkConf().setAppName(args(0))
    sparkConf.set("es.nodes", config.getString("elasticsearch.url"))
    sparkConf.set("es.index.auto.create", "false")

    val jobs = Map(
      "count" -> countBatch,
      "similarity" -> similarityBatch,
      "user" -> userBatch,
      "count-streaming" -> countStreaming,
      "user-streaming" -> userStreaming)

    (for {
      algo <- args lift 0
      job <- jobs get algo
    } yield {
      job.run(config, sparkConf, args)
    }) getOrElse {
      println("""|Usage:
                 |  submit count           [path] e.g. "2015/*/*"
                 |  submit similarity      [path] 
                 |  submit user            [path]
                 |  submit count-streaming
                 |  submit user-streaming
                 |""".stripMargin)
    }
  }
}

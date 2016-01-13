package blah.algo

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.SparkConf
import org.apache.kafka.common.serialization.StringSerializer
import com.softwaremill.react.kafka.ProducerProperties
import com.typesafe.config.ConfigFactory
import blah.core.KafkaProducer

object Submit {
  def main(args: Array[String]) {
    val config = ConfigFactory.load()
    lazy val producer = KafkaProducer(ProducerProperties(
      bootstrapServers = config.getString("producer.broker.list"),
      valueSerializer = new StringSerializer,
      topic = "trainings"))

    lazy val countAlgo = new CountAlgo
    lazy val similarityAlgo = new SimilarityAlgo
    lazy val userAlgo = new UserAlgo

    lazy val countBatch = new BatchJob("count", countAlgo, producer)
    lazy val similarityBatch = new BatchJob("similarity", similarityAlgo, producer)
    lazy val userBatch = new BatchJob("user", userAlgo, producer)
    lazy val countStreaming = new CountStreamingJob("count", countAlgo, producer)
    lazy val userStreaming = new StreamingJob("user", userAlgo, producer)
    
    lazy val jobs = Map(
      "count" -> countBatch,
      "similarity" -> similarityBatch,
      "user" -> userBatch,
      "count-streaming" -> countStreaming,
      "user-streaming" -> userStreaming)

    (for {
      algo <- args lift 0
      job <- jobs get algo
    } yield {
      val sparkConf = new SparkConf()
        .setMaster(config.getString("spark.master"))
        .setAppName(algo)
      sparkConf.set("es.nodes", config.getString("elasticsearch.url"))
      sparkConf.set("es.index.auto.create", "false")
      job.run(config, sparkConf, args)
      sys exit 0
    }) getOrElse {
      println(s"""|No such command: ${args.mkString(" ")}
                  |Usage:
                  |  submit count           [path] e.g. "2015/*/*"
                  |  submit similarity      [path] 
                  |  submit user            [path]
                  |  submit count-streaming
                  |  submit user-streaming
                  |""".stripMargin)
      sys exit 1
    }
  }
}

package blah.algo

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.SparkConf
import com.softwaremill.react.kafka.ProducerProperties
import com.typesafe.config.ConfigFactory
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder

object Submit {
  def main(args: Array[String]) {
    val arguments = args.map(_ split " ").flatten
    val config = ConfigFactory.load()

    val producer = KafkaProducer[String](ProducerProperties(
      brokerList = config.getString("producer.broker.list"),
      topic = "trainings",
      clientId = "websocket",
      encoder = new StringEncoder))

    lazy val countAlgo = new CountAlgo
    lazy val similarityAlgo = new SimilarityAlgo
    lazy val userAlgo = new UserAlgo
    lazy val funnelAlgo = new FunnelAlgo

    // lazy val countBatch = new BatchJob("count", countAlgo, producer)
    // lazy val similarityBatch = new BatchJob("similarity", similarityAlgo, producer)
    // lazy val userBatch = new BatchJob("user", userAlgo, producer)
    // lazy val funnelBatch = new BatchJob("funnel", funnelAlgo, producer)
    // lazy val countStreaming = new CountStreamingJob("count", countAlgo, producer)
    // lazy val userStreaming = new UserStreamingJob("user", userAlgo, producer)

    lazy val jobs = Map.empty[String, Job]
      // "count" -> countBatch,
      // "similarity" -> similarityBatch,
      // // "user" -> userBatch,
      // "funnel" -> funnelBatch,
      // "count-streaming" -> countStreaming)
      // "user-streaming" -> userStreaming)

    (for {
      algo <- arguments lift 0
      job <- jobs get algo
    } yield {
      val sparkConf = new SparkConf()
        .setMaster(config.getString("spark.master"))
        .setAppName(algo)
      sparkConf.set("es.nodes", config.getString("elasticsearch.url"))
      sparkConf.set("es.index.auto.create", "false")
      try {
        job.run(config, sparkConf, arguments drop 1)
        sys exit 0
      } catch {
        case e: IllegalArgumentException =>
          println(e.getMessage)
          sys exit 1
      }
    }) getOrElse {
      println(s"""|Error: No such command: ${arguments.mkString(" ")}
                  |Usage: java -jar algo.jar submit COMMAND [OPTION]
                  |Commands:
                  |  count
                  |  similarity
                  |  user
                  |  funnel
                  |  count-streaming
                  |  user-streaming
                  |""".stripMargin)
      sys exit 1
    }
  }
}

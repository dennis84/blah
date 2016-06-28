package blah.algo

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.SparkConf
import com.typesafe.config.ConfigFactory
import kafka.serializer.StringEncoder

object Submit {
  def main(args: Array[String]) {
    val arguments = args.map(_ split " ").flatten
    val config = ConfigFactory.load()

    lazy val countAlgo = new CountAlgo
    lazy val similarityAlgo = new SimilarityAlgo
    lazy val userAlgo = new UserAlgo
    lazy val funnelAlgo = new FunnelAlgo
    lazy val referrerAlgo = new ReferrerAlgo

    lazy val countBatch = new BatchJob("count", countAlgo)
    lazy val similarityBatch = new BatchJob("similarity", similarityAlgo)
    lazy val userBatch = new BatchJob("user", userAlgo)
    lazy val funnelBatch = new BatchJob("funnel", funnelAlgo)
    lazy val referrerBatch = new BatchJob("referrer", referrerAlgo)
    lazy val countStreaming = new CountStreamingJob("count", countAlgo)
    lazy val userStreaming = new UserStreamingJob("user", userAlgo)

    lazy val jobs = Map(
      "count" -> countBatch,
      "similarity" -> similarityBatch,
      "user" -> userBatch,
      "funnel" -> funnelBatch,
      "referrer" -> referrerBatch,
      "count-streaming" -> countStreaming,
      "user-streaming" -> userStreaming)

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
                  |  referrer
                  |  count-streaming
                  |  user-streaming
                  |""".stripMargin)
      sys exit 1
    }
  }
}

package blah.algo

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.SparkConf
import com.typesafe.config.ConfigFactory

object Submit {
  def main(args: Array[String]) {
    val arguments = args.map(_ split " ").flatten
    val config = ConfigFactory.load()

    lazy val countAlgo = new CountAlgo
    lazy val userAlgo = new UserAlgo
    lazy val funnelAlgo = new FunnelAlgo
    lazy val referrerAlgo = new ReferrerAlgo
    lazy val recommendationAlgo = new RecommendationAlgo
    lazy val similarityAlgo = new SimilarityAlgo
    lazy val collectionCountAlgo = new CollectionCountAlgo

    lazy val countBatch = new BatchJob("count", countAlgo)
    lazy val userBatch = new BatchJob("user", userAlgo)
    lazy val funnelBatch = new BatchJob("funnel", funnelAlgo)
    lazy val referrerBatch = new BatchJob("referrer", referrerAlgo)
    lazy val recommendationBatch = new BatchJob("recommendation", recommendationAlgo)
    lazy val similarityBatch = new BatchJob("similarity", similarityAlgo)
    lazy val countStreaming = new CountStreamingJob("count", countAlgo)
    lazy val userStreaming = new UserStreamingJob("user", userAlgo)
    lazy val collectionCountStreaming =
      new StreamingJob("collection_count", collectionCountAlgo)

    lazy val jobs = Map(
      "count" -> countBatch,
      "user" -> userBatch,
      "funnel" -> funnelBatch,
      "referrer" -> referrerBatch,
      "recommendation" -> recommendationBatch,
      "similarity" -> similarityBatch,
      "count-streaming" -> countStreaming,
      "user-streaming" -> userStreaming,
      "collection-count-streaming" -> collectionCountStreaming)

    (for {
      algo <- arguments lift 0
      job <- jobs get algo
    } yield {
      val sparkConf = new SparkConf()
        .setMaster(config.getString("spark.master"))
        .setAppName(algo)
      sparkConf.set("elastic.url", config.getString("elasticsearch.url"))
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
                  |  user
                  |  funnel
                  |  referrer
                  |  recommendation
                  |  similarity
                  |  count-streaming
                  |  user-streaming
                  |  collection-count-streaming
                  |""".stripMargin)
      sys exit 1
    }
  }
}

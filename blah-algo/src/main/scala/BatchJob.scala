package blah.algo

import scala.util.{Try, Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class BatchJob(
  config: Config,
  algo: Algo,
  producer: KafkaProducer[String],
  message: String
) extends Job {

  def run(conf: SparkConf, args: Array[String]) {
    val path = args.lift(1).getOrElse("*/*/*")
    val hadoopUrl = config.getString("hadoop.url")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(s"$hadoopUrl/events/$path/*.jsonl")
    algo.train(rdd)

    Try(producer send message) match {
      case Success(_) => println("Successfully sent message")
      case Failure(e) => println("Message could not be sent")
    }

    sc.stop
  }
}

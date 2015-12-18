package blah.algo

import scala.util.{Try, Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class BatchJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[String]
) extends Job {

  def run(config: Config, sparkConf: SparkConf, args: Array[String]) {
    val path = args.lift(1).getOrElse("*/*/*")
    val hadoopUrl = config.getString("hadoop.url")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.textFile(s"$hadoopUrl/events/$path/*.jsonl")

    algo.train(rdd).map { doc =>
      (Map(ID -> doc.id), doc.data)
    }.saveToEsWithMeta(s"blah/$name")

    Try(producer send name) match {
      case Success(_) => println("Successfully sent message")
      case Failure(e) => println("Message could not be sent")
    }

    sc.stop
  }
}

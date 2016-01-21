package blah.algo

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import akka.util.ByteString
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.io.{LongWritable, BytesWritable}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import com.typesafe.config.Config
import blah.core.KafkaProducer

class BatchJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[Array[Byte], String]
) extends Job {

  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    val path = args.lift(1).getOrElse("*/*/*")
    val hadoopUrl = s"${config getString "hadoop.url"}/events/$path/*.jsonl"
    val sc = new SparkContext(sparkConf)
    val rdd = sc.sequenceFile[LongWritable, BytesWritable](hadoopUrl)
      .map(x => ByteString(x._2.copyBytes).utf8String)

    algo.train(rdd).map { doc =>
      (Map(ID -> doc.id), doc.data)
    }.saveToEsWithMeta(s"blah/$name")

    producer send name
    sc.stop
  }
}

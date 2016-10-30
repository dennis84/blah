package blah.algo

import java.util.Properties
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Success, Failure}
import akka.util.ByteString
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.io.{LongWritable, BytesWritable}
import org.apache.kafka.clients.producer.ProducerRecord
import com.typesafe.config.Config
import DatasetKafkaWriter._
import DatasetElasticWriter._
import FindOpt._

class BatchJob[T <: Product : TypeTag](
  name: String,
  algo: Algo[T]
)(implicit ct: ClassTag[T]) extends Job with java.io.Serializable {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    val path = (args opt "path").getOrElse("*/*/*")
    val hadoopUrl = s"${config getString "hadoop.url"}/events/$path/*.jsonl"
    val sc = new SparkContext(sparkConf)
    val sparkSession = SparkSession.builder.config(sparkConf).getOrCreate()
    val rdd = sc.sequenceFile[LongWritable, BytesWritable](hadoopUrl)
      .map(x => ByteString(x._2.copyBytes).utf8String)
    val output = algo.train(rdd, sparkSession, args shift "path")

    output.writeToElastic("blah", name)

    val props = new Properties
    props.put("bootstrap.servers", config.getString("producer.broker.list"))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    output.toJSON.writeToKafka(props, x =>
      new ProducerRecord[String, String]("trainings", s"$name@$x"))

    sc.stop
  }
}

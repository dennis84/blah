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
import org.elasticsearch.spark.sql._
import com.typesafe.config.Config
import kafka.producer.KeyedMessage
import blah.core.FindOpt._
import RddKafkaWriter._

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

    output.saveToEs(s"blah/$name", Map(
      "es.mapping.id" -> "id",
      "es.mapping.exclude" -> "id"))

    val props = new Properties
    props.put("metadata.broker.list", config.getString("producer.broker.list"))
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("key.serializer.class", "kafka.serializer.StringEncoder")

    output.toJSON.rdd.writeToKafka(props, x =>
      new KeyedMessage[String, String]("trainings", s"$name@$x"))

    sc.stop
  }
}

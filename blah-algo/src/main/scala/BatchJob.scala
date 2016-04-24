package blah.algo

import java.util.Properties
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import akka.util.ByteString
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.hadoop.io.{LongWritable, BytesWritable}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import com.typesafe.config.Config
import kafka.producer.KeyedMessage
import blah.core.FindOpt._
import RddKafkaWriter._

class BatchJob(name: String, algo: Algo) extends Job {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    val path = (args opt "path").getOrElse("*/*/*")
    val hadoopUrl = s"${config getString "hadoop.url"}/events/$path/*.jsonl"
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    val rdd = sc.sequenceFile[LongWritable, BytesWritable](hadoopUrl)
      .map(x => ByteString(x._2.copyBytes).utf8String)
    val output = algo.train(rdd, sqlContext, args shift "path")
      
    output map { case (id, doc) =>
      (Map(ID -> id), doc)
    } saveToEsWithMeta s"blah/$name"

    val props = new Properties
    props.put("metadata.broker.list", config.getString("producer.broker.list"))
    props.put("serializer.class", "kafka.serializer.DefaultEncoder")
    props.put("key.serializer.class", "kafka.serializer.StringEncoder")

    output.writeToKafka(props, x =>
      new KeyedMessage[String, Array[Byte]]("trainings", null, name.getBytes))

    sc.stop
  }
}

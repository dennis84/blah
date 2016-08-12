package blah.algo

import java.util.Properties
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import com.typesafe.config.Config
import kafka.producer.KeyedMessage
import kafka.serializer.StringDecoder
import DatasetKafkaWriter._
import DatasetElasticWriter._

class StreamingJob[T <: Product : TypeTag](
  name: String,
  algo: Algo[T]
)(implicit ct: ClassTag[T]) extends Job with java.io.Serializable {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    val ssc = new StreamingContext(sparkConf,
      Seconds(config.getInt("streaming.batch.interval")))
    val stream = KafkaUtils
      .createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
        Map("metadata.broker.list" -> config.getString("consumer.broker.list")),
        Set("events")).map(_._2)

    stream.foreachRDD { rdd =>
      val sparkSession = SparkSessionSingleton
        .getInstance(rdd.sparkContext.getConf)
      val output = algo.train(rdd, sparkSession, args)

      output.writeToElastic("blah", name)

      val props = new Properties
      props.put("metadata.broker.list", config.getString("producer.broker.list"))
      props.put("serializer.class", "kafka.serializer.StringEncoder")
      props.put("key.serializer.class", "kafka.serializer.StringEncoder")

      output.toJSON.writeToKafka(props, x =>
        new KeyedMessage[String, String]("trainings", s"$name@$x"))
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

object SparkSessionSingleton {
  @transient  private var instance: SparkSession = _

  def getInstance(conf: SparkConf): SparkSession = {
    if(instance == null) {
      instance = SparkSession.builder.config(conf).getOrCreate()
    }
    instance
  }
}

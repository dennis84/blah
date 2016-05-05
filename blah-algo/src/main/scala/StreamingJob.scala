package blah.algo

import java.util.Properties
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import com.typesafe.config.Config
import kafka.producer.KeyedMessage
import kafka.serializer.StringDecoder
import RddKafkaWriter._

class StreamingJob[T <: Product : TypeTag](
  name: String,
  algo: Algo[T]
)(implicit ct: ClassTag[T]) extends Job with java.io.Serializable {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    sparkConf.set("es.write.operation", "upsert")

    val ssc = new StreamingContext(sparkConf,
      Seconds(config.getInt("streaming.batch.interval")))
    val stream = KafkaUtils
      .createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
        Map("metadata.broker.list" -> config.getString("consumer.broker.list")),
        Set("events")).map(_._2)

    stream.foreachRDD { rdd =>
      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
      val output = algo.train(rdd, sqlContext, args)
      import sqlContext.implicits._

      output map { case (id, doc: Any) =>
        Map(ID -> id) -> doc
      } saveToEsWithMeta s"blah/$name"

      val props = new Properties
      props.put("metadata.broker.list", config.getString("producer.broker.list"))
      props.put("serializer.class", "kafka.serializer.StringEncoder")
      props.put("key.serializer.class", "kafka.serializer.StringEncoder")

      output.map(_._2).toDF.toJSON.writeToKafka(props, x =>
        new KeyedMessage[String, String]("trainings", s"$name@$x"))
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

object SQLContextSingleton {
  @transient  private var instance: SQLContext = _

  def getInstance(sc: SparkContext): SQLContext = {
    if(instance == null) {
      instance = new SQLContext(sc)
    }
    instance
  }
}

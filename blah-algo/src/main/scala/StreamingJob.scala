package blah.algo

import java.util.Properties
import scala.concurrent.ExecutionContext
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

class StreamingJob[T](name: String, algo: Algo[T]) extends Job {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    sparkConf.set("es.write.operation", "upsert")

    val ssc = new StreamingContext(sparkConf, Seconds(10))
    val stream = KafkaUtils
      .createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
        Map("metadata.broker.list" -> config.getString("consumer.broker.list")),
        Set("events")).map(_._2)

    stream.foreachRDD { rdd =>
      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
      val output = algo.train(rdd, sqlContext, args)
      import sqlContext.implicits._

      output.rdd map { case (id, doc: Any) =>
        Map(ID -> id) -> doc
      } saveToEsWithMeta s"blah/$name"

      val props = new Properties
      props.put("metadata.broker.list", config.getString("producer.broker.list"))
      props.put("serializer.class", "kafka.serializer.DefaultEncoder")
      props.put("key.serializer.class", "kafka.serializer.StringEncoder")

      output.df.toJSON.writeToKafka(props, x =>
        new KeyedMessage[String, Array[Byte]]("trainings", null, x.getBytes))
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

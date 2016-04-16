package blah.algo

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import com.typesafe.config.Config
import kafka.producer.KafkaProducer
import kafka.serializer.StringDecoder

class StreamingJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[String]
) extends Job {

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
      import sqlContext.implicits._
      algo.train(rdd, sqlContext, args).map { case (id, doc) =>
        (Map(ID -> id), doc)
      }.saveToEsWithMeta(s"blah/$name")
      producer send name
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

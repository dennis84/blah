package blah.algo

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import org.apache.spark.SparkConf
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
      algo.train(rdd, args).map { doc =>
        (Map(ID -> doc.id), doc.data)
      }.saveToEsWithMeta(s"blah/$name")
      producer send name
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

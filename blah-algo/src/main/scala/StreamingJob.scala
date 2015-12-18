package blah.algo

import scala.util.{Try, Success, Failure}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class StreamingJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[String]
) extends Job {

  def run(config: Config, sparkConf: SparkConf, args: Array[String]) {
    sparkConf.set("es.write.operation", "upsert")

    val ssc = new StreamingContext(sparkConf, Seconds(10))
    val stream = KafkaUtils.createStream(ssc,
      config.getString("consumer.zookeeper.connect"),
      config.getString("consumer.group.id"),
      Map("events" -> 1)
    ).map(_._2)

    stream.foreachRDD { rdd =>
      algo.train(rdd).map { doc =>
        (Map(ID -> doc.id), doc.data)
      }.saveToEsWithMeta(s"blah/$name")

      Try(producer send name) match {
        case Success(_) => println("Successfully sent message")
        case Failure(e) => println("Message could not be sent")
      }
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

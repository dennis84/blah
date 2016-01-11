package blah.algo

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._
import com.typesafe.config.Config
import blah.core.KafkaProducer

class StreamingJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[Array[Byte], String]
) extends Job {

  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
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

      (producer send name) onComplete {
        case Success(_) => println("Successfully sent message")
        case Failure(e) => println("Message could not be sent")
      }
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

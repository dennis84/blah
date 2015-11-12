package blah.algo

import scala.util.{Try, Success, Failure}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class StreamingJob(
  config: Config,
  algo: Algo,
  producer: KafkaProducer[String],
  message: String
) extends Job {

  def run(conf: SparkConf, args: Array[String]) {
    conf.set("es.write.operation", "upsert")

    val ssc = new StreamingContext(conf, Seconds(10))
    val stream = KafkaUtils.createStream(ssc,
      config.getString("consumer.zookeeper.connect"),
      config.getString("consumer.group.id"),
      Map("events" -> 1)
    ).map(_._2)

    stream.foreachRDD { rdd =>
      algo.train(rdd)
      Try(producer send message) match {
        case Success(_) => println("Successfully sent message")
        case Failure(e) => println("Message could not be sent")
      }
    }

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

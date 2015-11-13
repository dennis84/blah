package blah.algo

import org.apache.spark.SparkConf
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class CountStreamingJob(
  algo: Algo,
  producer: KafkaProducer[String],
  message: String
) extends StreamingJob(algo, producer, message) {
  override def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  ) {
    sparkConf.set("es.update.script", "ctx._source.count += count")
    sparkConf.set("es.update.script.params", "count:count")
    super.run(config, sparkConf, args)
  }
}

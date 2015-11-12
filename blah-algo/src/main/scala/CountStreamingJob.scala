package blah.algo

import org.apache.spark.SparkConf
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class CountStreamingJob(
  config: Config,
  algo: Algo,
  producer: KafkaProducer[String],
  message: String
) extends StreamingJob(config, algo, producer, message) {

  override def run(conf: SparkConf, args: Array[String]) {
    conf.set("es.update.script", "ctx._source.count += count")
    conf.set("es.update.script.params", "count:count")
    super.run(conf, args)
  }
}

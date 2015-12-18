package blah.algo

import org.apache.spark.SparkConf
import kafka.producer.KafkaProducer
import com.typesafe.config.Config

class CountStreamingJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[String]
) extends StreamingJob(name, algo, producer) {
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

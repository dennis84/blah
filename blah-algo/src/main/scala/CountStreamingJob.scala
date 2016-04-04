package blah.algo

import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
import com.typesafe.config.Config
import kafka.producer.KafkaProducer

class CountStreamingJob(
  name: String,
  algo: Algo,
  producer: KafkaProducer[String]
) extends StreamingJob(name, algo, producer) {
  override def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    sparkConf.set("es.update.script", "ctx._source.count += count")
    sparkConf.set("es.update.script.params", "count:count")
    super.run(config, sparkConf, args)
  }
}

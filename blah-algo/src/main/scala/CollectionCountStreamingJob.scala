package blah.algo

import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
import com.typesafe.config.Config

class CollectionCountStreamingJob(
  name: String,
  algo: CollectionCountAlgo
) extends StreamingJob(name, algo) {
  override def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    sparkConf.set("elastic.script",
      """|ctx._source.count += params.count
         |""".stripMargin.replaceAll("\n", ""))
    super.run(config, sparkConf, args)
  }
}
package blah.algo

import org.apache.spark.SparkConf
import com.typesafe.config.Config

trait Job {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  ): Unit
}

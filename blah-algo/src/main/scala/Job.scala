package blah.algo

import org.apache.spark.SparkConf

trait Job {
  def run(conf: SparkConf, args: Array[String]): Unit
}

package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, Dataset}

trait Algo[T] extends java.io.Serializable {
  def train(
    rdd: RDD[String],
    sqlContext: SparkSession,
    args: Array[String]
  ): Dataset[T]
}

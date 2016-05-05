package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

trait Algo[T] extends java.io.Serializable {
  def train(
    rdd: RDD[String],
    sqlContext: SQLContext,
    args: Array[String]
  ): RDD[(String, T)]
}

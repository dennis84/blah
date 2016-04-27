package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.DataFrame

trait Algo[T] {
  def train(
    rdd: RDD[String],
    sqlContext: SQLContext,
    args: Array[String]
  ): Result[T]
}

case class Result[T](
  rdd: RDD[(String, T)],
  df: DataFrame)

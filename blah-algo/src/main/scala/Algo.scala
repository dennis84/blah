package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

trait Algo {
  def train(
    rdd: RDD[String],
    sqlContext: SQLContext,
    args: Array[String]
  ): RDD[(String, _)]
}

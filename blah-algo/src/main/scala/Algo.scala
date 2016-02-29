package blah.algo

import org.apache.spark.rdd.RDD

trait Algo {
  def train(rdd: RDD[String], args: Array[String]): RDD[Doc]
}

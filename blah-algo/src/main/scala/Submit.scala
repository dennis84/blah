package blah.algo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.io.{BytesWritable, LongWritable}

object Submit {
  def main(args: Array[String]) {
    val algos = Map(
      "count" -> new CountAlgo,
      "similarity" -> new SimilarityAlgo)
    val algo = algos(args(0))
    val conf = new SparkConf()
      .setAppName(args(0))
      .set("spark.cassandra.connection.host", "127.0.0.1")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("hdfs://localhost:9000/user/dennis/blah/events.*")
    algo.train(rdd)
    sc.stop
  }
}

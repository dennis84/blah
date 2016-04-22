package blah.algo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

trait SparkFun {
  def withSparkContext(test: (SparkContext, SQLContext) => Any) {
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    try {
      test(sc, sqlContext)
    } finally {
      sc.stop()
    }
  }
}

package blah.algo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

trait SparkFun {
  def withSparkContext(test: SQLContext => Any): Unit =
    withSparkContext(new SparkConf()
      .setMaster("local[2]")
      .setAppName(this.getClass.getName))(test)

  def withSparkContext(conf: SparkConf)(test: SQLContext => Any): Unit = {
    val sc = new SparkContext(conf)
    try {
      test(new SQLContext(sc))
    } finally {
      sc.stop()
    }
  }
}

package blah.algo

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession

trait SparkFun {
  def withSparkContext(test: SparkSession => Any): Unit =
    withSparkContext(new SparkConf()
      .setMaster("local")
      .setAppName(this.getClass.getName))(test)

  def withSparkContext(conf: SparkConf)(test: SparkSession => Any): Unit = {
    val s = SparkSession.builder.config(conf).getOrCreate()
    try {
      test(s)
    } finally {
      s.stop()
    }
  }
}

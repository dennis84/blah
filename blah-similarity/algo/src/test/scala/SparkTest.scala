package blah.similarity

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession

trait SparkTest {
  def withSparkSession(test: SparkSession => Any): Unit =
    withSparkSession(new SparkConf()
      .setMaster("local")
      .setAppName(this.getClass.getName))(test)

  def withSparkSession(conf: SparkConf)(test: SparkSession => Any): Unit = {
    val s = SparkSession.builder.config(conf).getOrCreate()
    try {
      test(s)
    } finally {
      s.stop()
    }
  }
}

package blah.algo

import org.apache.spark.{SparkConf, SparkContext}

trait SparkFun {

  def withSparkContext(test: SparkContext => Any) {
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName(this.getClass.getName)
    val sc = new SparkContext(conf)
    try {
      test(sc)
    } finally {
      sc.stop()
    }
  }
}

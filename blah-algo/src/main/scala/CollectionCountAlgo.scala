package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class CollectionCountAlgo extends Algo[CollectionCount] {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(CollectionCountSchema())
    reader.json(rdd).createOrReplaceTempView("collection_count")
    ctx.sql("""|SELECT
               |  collection AS name
               |FROM collection_count""".stripMargin)
      .groupBy("name")
      .count()
      .as[CollectionCount]
  }
}

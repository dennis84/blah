package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row

class CollectionCountAlgo extends Algo[CollectionCount] {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(CollectionCountSchema())
    reader.json(rdd).createOrReplaceTempView("collection_count")
    ctx.sql("""|SELECT
               |  date,
               |  collection AS name
               |FROM collection_count""".stripMargin)
      .filter("date is not null")
      .map { case Row(date: String, name: String) =>
        val d = ZonedDateTime.parse(date)
          .plusSeconds(1)
          .truncatedTo(ChronoUnit.SECONDS)
          .toString
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((d + name).hashCode)
          .array)
        CollectionCount(uuid.toString, name, d)
      }
      .groupBy("id", "date", "name")
      .count()
      .as[CollectionCount]
  }
}

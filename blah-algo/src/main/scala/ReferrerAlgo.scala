package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.temporal.ChronoUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, Row}
import blah.core.{UserAgent, UserAgentClassifier}
import blah.core.FindOpt._

class ReferrerAlgo extends Algo[Referrer] {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._

    val where = args opt "collection" map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    val reader = ctx.read.schema(ReferrerSchema())
    reader.json(rdd).createOrReplaceTempView("referrer")
    ctx.sql(s"""|SELECT
                |  collection,
                |  props.referrer AS referrer
                |FROM referrer $where""".stripMargin)
      .filter("referrer is not null")
      .groupBy("collection", "referrer")
      .count()
      .map { case Row(coll: String, ref: String, count: Long) =>
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((coll + ref).hashCode)
          .array)
        (uuid.toString, Referrer(coll, ref, count))
      }
      .rdd
  }
}

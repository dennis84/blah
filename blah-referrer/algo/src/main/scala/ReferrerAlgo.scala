package blah.referrer

import java.util.UUID
import java.nio.ByteBuffer
import java.time.temporal.ChronoUnit
import org.apache.spark.sql.{SparkSession, Row}
import FindOpt._

object ReferrerAlgo {
  def train(ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._

    val where = args opt "collection" map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    ctx.sql(s"""|SELECT
                |  collection,
                |  props.referrer AS referrer
                |FROM events $where""".stripMargin)
      .filter("referrer is not null")
      .groupBy("collection", "referrer")
      .count()
      .map { case Row(coll: String, ref: String, count: Long) =>
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((coll + ref).hashCode)
          .array)
        Referrer(uuid.toString, coll, ref, count)
      }
  }
}

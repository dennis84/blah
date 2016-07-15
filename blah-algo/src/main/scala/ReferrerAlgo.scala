package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.temporal.ChronoUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Row}
import blah.core.{UserAgent, UserAgentClassifier}
import blah.core.FindOpt._

class ReferrerAlgo extends Algo[Referrer] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._

    val where = args opt "collection" map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    val reader = ctx.read.schema(ReferrerSchema())
    reader.json(rdd).registerTempTable("referrer")
    ctx.sql(s"""|SELECT
                |  collection,
                |  props.referrer AS referrer
                |FROM referrer $where""".stripMargin)
      .filter("referrer is not null")
      .map { case Row(collection: String, referrer: String) =>
        ((collection, referrer), 1)
      }
      .reduceByKey(_ + _)
      .map { case((collection, referrer), count) =>
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((collection + referrer).hashCode)
          .array)
        (uuid.toString, Referrer(collection, referrer, count))
      }
  }
}

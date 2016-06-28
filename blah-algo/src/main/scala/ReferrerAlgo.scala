package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.temporal.ChronoUnit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Row}
import blah.core.{UserAgent, UserAgentClassifier}

class ReferrerAlgo extends Algo[Referrer] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(ReferrerSchema())
    reader.json(rdd).registerTempTable("referrer")
    ctx.sql("""|SELECT
               |  props.referrer AS referrer
               |FROM referrer""".stripMargin)
      .filter("referrer is not null")
      .map { case Row(referrer: String) => (referrer, 1) }
      .reduceByKey(_ + _)
      .map { case(referrer, count) =>
        (referrer, Referrer(referrer, count))
      }
  }
}

package blah.collection

import java.util.UUID
import java.nio.ByteBuffer
import java.time.{ZonedDateTime, ZoneId}
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.sql.Timestamp
import org.apache.spark.sql.{SparkSession, Row}

object CollectionCountAlgo {
  def train(ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    ctx.sql("SELECT date, collection FROM events")
      .filter("date is not null")
      .map { case Row(date: Timestamp, name: String) =>
        val d = date.toInstant.atZone(ZoneId.of("UTC"))
          .plusSeconds(1)
          .truncatedTo(ChronoUnit.SECONDS)
          .format(DateTimeFormatter.ISO_INSTANT)
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

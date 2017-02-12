package blah.funnel

import java.util.UUID
import java.nio.ByteBuffer
import java.time.ZonedDateTime
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, Dataset, Row}
import FindOpt._

object FunnelAlgo {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    val config = (for {
      name <- args opt "name"
      steps <- args opt "steps" map (_ split ",")
    } yield {
      FunnelConfig(name, steps.toList)
    }) getOrElse {
      throw new java.lang.IllegalArgumentException("Invalid arguments")
    }

    val reader = ctx.read.schema(FunnelSchema())
    reader.json(rdd).createOrReplaceTempView("funnel")

    val ord = Ordering[Long].on[String](x =>
      ZonedDateTime.parse(x).toInstant.toEpochMilli)

    ctx.sql("""|SELECT
               |  date,
               |  props.user AS user,
               |  props.item AS item
               |FROM funnel""".stripMargin)
      .filter("user is not null and item is not null")
      .as[FunnelEvent]
      .groupByKey(_.user)
      .flatMapGroups { case(user, xs) =>
        val eventsByUser = (xs.toList.sortBy(_.date)(ord)
          .dropWhile(x => x.item != config.steps.head)
          .span(_.item != config.steps.last) match {
            case(head, tail) => head ::: tail.take(1)
          })
          .foldRight(List.empty[FunnelEvent])((x, a) => a match {
            case h :: xs if(h.item == x.item) => a
            case _ => x :: a
          })

        eventsByUser.zip(None +: eventsByUser.map(Option(_)))
      }
      .groupBy("_1.item", "_2.item")
      .count()
      .map { row =>
        val item = row.getString(0)
        val parent = Option(row.getString(1))
        val count = row.getLong(2)
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((config.name + item + parent.getOrElse("null")).hashCode)
          .array)
        Funnel(uuid.toString, config.name, item, parent, count)
      }
  }
}

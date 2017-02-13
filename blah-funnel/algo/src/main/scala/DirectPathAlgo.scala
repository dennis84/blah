package blah.funnel

import java.util.UUID
import java.nio.ByteBuffer
import java.time.{ZonedDateTime, ZoneId}
import java.sql.Timestamp
import org.apache.spark.sql.SparkSession
import FindOpt._

object DirectPathAlgo {
  def train(ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    val config = (for {
      name <- args opt "name"
      steps <- args opt "steps" map (_ split ",")
    } yield {
      FunnelConfig(name, steps.toList)
    }) getOrElse {
      throw new java.lang.IllegalArgumentException("Invalid arguments")
    }

    val ord = Ordering[Long].on[Timestamp](x => x.toInstant.toEpochMilli)

    ctx.sql("""|SELECT
               |  date,
               |  props.user AS user,
               |  props.item AS item
               |FROM events""".stripMargin)
      .filter("user is not null and item is not null")
      .as[FunnelEvent]
      .groupByKey(_.user)
      .flatMapGroups { case(user, xs) =>
        val path = (xs.toList.sortBy(_.date)(ord)
          .dropWhile(x => x.item != config.steps.head)
          .span(_.item != config.steps.last) match {
            case(head, tail) => head ::: tail.take(1)
          })

        val eventsByUser = path
          .foldLeft(List.empty[FunnelEvent])((acc, x) => acc match {
            case _ if(acc.lastOption.map(_.item) == Some(x.item)) => acc
            case _ => {
              val slice = acc.map(_.item)
              if(config.steps.containsSlice(slice)) acc ::: List(x)
              else acc
            }
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

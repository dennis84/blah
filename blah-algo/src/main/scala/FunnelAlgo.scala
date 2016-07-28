package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.ZonedDateTime
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import blah.core.FindOpt._

class FunnelAlgo extends Algo[Funnel] {
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
    val events = ctx.sql("""|SELECT
                            |  date,
                            |  props.user AS user,
                            |  props.item AS item
                            |FROM funnel""".stripMargin)
      .filter("user is not null and item is not null")
      .map(FunnelEvent(_))
      .rdd

    val ord = Ordering[Long].on[String](x =>
      ZonedDateTime.parse(x).toInstant.toEpochMilli)

    val eventsByUser = events.groupBy(_.user)
      .collect { case (Some(user), xs) =>
        val items = (xs.toList.sortBy(_.date)(ord)
          .dropWhile(x => x.item.get != config.steps.head)
          .span(_.item.get != config.steps.last) match {
            case(head, tail) => head ::: tail.take(1)
          })
          .foldRight(List.empty[FunnelEvent])((x, a) => a match {
            case h :: xs if(h.item == x.item) => a
            case _ => x :: a
          })
        (user, items)
      } filter (x => !x._2.isEmpty)

    eventsByUser flatMap { case(_, items) =>
      items.zip(None +: items.map(Option(_)))
    } collect { case (FunnelEvent(_, _, item), parent) =>
      ((item.get, parent.map(_.item.get)), 1)
    } reduceByKey ((a, b) => a + b) map { case((item, parent), count) =>
      val uuid = UUID.nameUUIDFromBytes(ByteBuffer
        .allocate(Integer.SIZE / 8)
        .putInt((config.name + item + parent).hashCode)
        .array)
      (uuid.toString, Funnel(config.name, item, parent, count))
    }
  }
}

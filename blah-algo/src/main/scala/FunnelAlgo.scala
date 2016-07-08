package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import java.time.ZonedDateTime
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import blah.core.FindOpt._

class FunnelAlgo extends Algo[Funnel] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
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
    reader.json(rdd).registerTempTable("funnel")
    val events = ctx.sql("""|SELECT
                            |  date,
                            |  props.user AS user,
                            |  props.item AS item
                            |FROM funnel""".stripMargin)
      .filter("user is not null and item is not null")
      .map(FunnelEvent(_))

    val ord = Ordering[Long]
      .on[ZonedDateTime](_.toInstant.toEpochMilli)

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
      items.zipAll(items.tail.map(Option(_)), None, None)
    } collect { case (FunnelEvent(_, _, item), maybeNext) =>
      ((item.get, maybeNext.map(_.item.get)), 1)
    } reduceByKey (_ + _) map { case((item, next), count) =>
      val uuid = UUID.nameUUIDFromBytes(ByteBuffer
        .allocate(Integer.SIZE / 8)
        .putInt((config.name + item + next).hashCode)
        .array)
      (uuid.toString, Funnel(config.name, item, next, count))
    }
  }
}

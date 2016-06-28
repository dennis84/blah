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

    val eventsByUser = events
      .groupBy(_.user)
      .collect { case(Some(user), xs) =>
        (user, xs.toList.sortBy(_.date)(ord))
      }

    val allSteps = (List(config.steps) /: config.steps) {
      (a,x) => a ::: List(a.last dropRight 1)
    }

    val paths = eventsByUser
      .flatMap { case(user, events) =>
        val items = events map (_.item.get)
        val ys = (items :\ List.empty[String])((x, a) => a match {
          case h :: xs if(h == x) => a
          case _ => x :: a
        })

        allSteps.collect {
          case x if(x.length > 0 && ys.containsSlice(x)) =>
            val index = ys.indexOfSlice(x)
            (ys.slice(index, index + x.length), 1)
        }
      }

    paths
      .filter(_._1.length > 0)
      .reduceByKey(_ + _)
      .map { case(path, count) =>
        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((config.name +: path).hashCode)
          .array)
        (uuid.toString, Funnel(config.name, path, count))
      }
  }
}

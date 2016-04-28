package blah.algo

import java.security.MessageDigest
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
                            |  props.item AS item,
                            |  props.referrer
                            |FROM funnel""".stripMargin)
      .filter("user is not null and item is not null")
      .map(FunnelEvent(_))

    val ord = Ordering[Long]
      .on[ZonedDateTime](_.toInstant.toEpochMilli)

    val users = events
      .groupBy(_.user)
      .collect { case(Some(user), xs) =>
        (user, xs.toList.sortBy(_.date)(ord))
      }

    val allSteps = (List(config.steps) /: config.steps) {
      (a,x) => a ::: List(a.last dropRight 1)
    }

    val paths = users
      .map { case(user, xs) =>
        val ys = (xs.map(_.item.get) :\ List.empty[String])((x, a) => a match {
          case h :: xs if(h == x) => a
          case _ => x :: a
        })

        allSteps collectFirst {
          case x if(x.length > 0 && ys.containsSlice(x)) =>
            val index = ys.indexOfSlice(x)
            (ys.slice(index, index + x.length), 1)
        } getOrElse (Nil, 0)
      }

    val output = paths
      .filter(_._1.length > 0)
      .reduceByKey(_ + _)
      .map { case(path, count) =>
        val id = MessageDigest.getInstance("SHA-1")
          .digest((config.name + path.mkString).getBytes("UTF-8"))
          .map("%02x".format(_))
          .mkString
        (id, Funnel(config.name, path, count))
      }

    Result(output, output.map(_._2).toDF)
  }
}

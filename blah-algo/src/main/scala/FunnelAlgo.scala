package blah.algo

import java.security.MessageDigest
import java.time.{ZonedDateTime, ZoneOffset}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Row}
import org.apache.spark.sql.types.{StructType,StructField,StringType}
import blah.core.FindOpt._

case class Funnel(
  name: String,
  path: List[String] = Nil,
  count: Long = 0)

case class FunnelEvent(
  date: ZonedDateTime,
  user: Option[String] = None,
  item: Option[String] = None,
  referrer: Option[String] = None)

object FunnelEvent {
  def apply(r: Row): FunnelEvent = FunnelEvent(
    ZonedDateTime.parse(r.getString(0)),
    Option(r.getString(1)),
    Option(r.getString(2)),
    Option(r.getString(3)))
}

object FunnelSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("props", StructType(Array(
      StructField("user", StringType, true),
      StructField("item", StringType, true),
      StructField("referrer", StringType, true))), true)))
}

case class FunnelConfig(
  name: String,
  steps: List[String] = Nil)

class FunnelAlgo {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
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
                            |  props.user,
                            |  props.item,
                            |  props.referrer
                            |FROM funnel""".stripMargin)
      .map(FunnelEvent(_))
      .filter(x => x.user.isDefined && x.item.isDefined)

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

    paths
      .filter(_._1.length > 0)
      .reduceByKey(_ + _)
      .map { case(path, count) =>
        val id = MessageDigest.getInstance("SHA-1")
          .digest((config.name + path.mkString).getBytes("UTF-8"))
          .map("%02x".format(_))
          .mkString
        (id, Funnel(config.name, path, count))
      }
  }
}

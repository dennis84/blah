package blah.algo

import java.security.MessageDigest
import java.time.{ZonedDateTime, ZoneOffset}
import scala.util.Try
import org.apache.spark.rdd.RDD
import spray.json._
import blah.core._
import JsonProtocol._
import FindOpt._

case class FunnelConfig(
  name: String,
  steps: List[String] = Nil)

class FunnelAlgo extends Algo {
  def train(rdd: RDD[String], args: Array[String]) = {
    val config = (for {
      name <- args opt "name"
      steps <- args opt "steps" map (_ split ",")
    } yield {
      FunnelConfig(name, steps.toList)
    }) getOrElse {
      throw new java.lang.IllegalArgumentException("Invalid arguments")
    }

    val views = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)

    val ord = Ordering[Long]
      .on[ZonedDateTime](_.toInstant.toEpochMilli)

    val users = views
      .map(x => (x.props.user, (x.props.item, x.props.referrer, x.date)))
      .groupByKey
      .map { case(user, xs) =>
        (user, xs.toList.sortBy(_._3)(ord))
      }

    val allSteps = (List(config.steps) /: config.steps) {
      (a,x) => a ::: List(a.last dropRight 1)
    }

    val paths = users
      .map { case(user, xs) =>
        val ys = (xs.map(_._1) :\ List.empty[String])((x, a) => a match {
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
        Doc(id, Map(
          "name" -> config.name,
          "path" -> path,
          "count" -> count
        ))
      }
  }
}

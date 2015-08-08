package blah.algo

import scala.util.Try
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._
import com.datastax.spark.connector._
import spray.json._
import blah.core._
import JsonProtocol._

class SimilarityAlgo extends Algo {
  def train {
    val conf = new SparkConf()
      .setAppName("similarity")
      .set("spark.cassandra.connection.host", "127.0.0.1")
    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://localhost:9000/user/dennis/blah/events/*")

    val events = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map(x => (x.props.user, x.props.event))

    val usersRDD = events.groupByKey()
    val users = usersRDD.keys.collect.toList

    val itemsRDD = events.groupBy(_._2)
    val items = itemsRDD.keys.collect.toList

    val views: RDD[(Int, Int)] = events
      .map(x => (users.indexOf(x._1), items.indexOf(x._2)))

    val rows = views.map { case (u,i) =>
      (u, (i, 1.0))
    }.groupByKey().map { case (u, is) =>
      val xs = is.groupBy(_._1).map { case (i, group) =>
        val r = group.reduce((a,b) => a)//(a._1, a._2 + b._2))
        (i, r._2)
      }.toArray
      Vectors.sparse(items.length, xs.map(_._1), xs.map(_._2))
    }

    val mat = new RowMatrix(rows)
    val sim = mat.columnSimilarities(0.5)

    val all = sim.toIndexedRowMatrix.rows.flatMap { case IndexedRow(i,v) =>
      val vector = v.asInstanceOf[SparseVector]
      vector.indices.zip(vector.values).flatMap { case (j,s) =>
        Seq((i.toInt,(j,s)), (j,(i.toInt,s)))
      }
    }.groupByKey.collectAsMap

    val ord = Ordering[Double].reverse

    val out = usersRDD
      .map { case(u, elems) =>
        (u, elems.flatMap { elem =>
          all.get(items.indexOf(elem)) map { xs =>
            xs.toList
              .sortBy(_._2)(ord)
              .map(x => items(x._1))
              .filterNot(elems.toList.contains _)
          } getOrElse Nil
        })
      }

    out.saveToCassandra("blah", "sims", SomeColumns("user", "views"))
    sc.stop
  }
}

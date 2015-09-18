package blah.algo

import java.security.MessageDigest
import scala.util.Try
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.Metadata._          
import spray.json._
import blah.core._
import JsonProtocol._

class SimilarityAlgo extends Algo {
  def train(rdd: RDD[String]) {
    val events = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map(x => (x.props.user, x.props.page))

    val usersRDD = events.groupByKey()
    val users = usersRDD.keys.collect.toList

    val itemsRDD = events.groupBy(_._2)
    val items = itemsRDD.keys.collect.toList

    val views: RDD[(Int, Int)] = events
      .map(x => (users.indexOf(x._1), items.indexOf(x._2)))

    val rows = views.map { case (u,i) =>
      (u, (i, 5.0))
    }.groupByKey().map { case (u, is) =>
      val xs = is.groupBy(_._1).map { case (i, group) =>
        val r = group.reduce((a,b) => a) //(a._1, a._2 + b._2))
        (i, r._2)
      }.toArray
      Vectors.sparse(items.length, xs.map(_._1), xs.map(_._2))
    }

    val mat = new RowMatrix(rows)
    val sim = mat.columnSimilarities()

    val all = sim.toIndexedRowMatrix.rows.flatMap { case IndexedRow(i,v) =>
      val vector = v.asInstanceOf[SparseVector]
      vector.indices.zip(vector.values).flatMap { case (j,s) =>
        Seq((i.toInt,(j,s)), (j,(i.toInt,s)))
      }
    }.groupByKey.collectAsMap

    val ord = Ordering[Double].reverse

    val out = usersRDD
      .map { case(u, elems) =>
        val doc = Map("user" -> u, "views" -> elems.flatMap { elem =>
          all.get(items.indexOf(elem)) getOrElse Nil
        }.toList
          .map(x => (items(x._1), x._2))
          .filterNot(x => elems.toList.contains(x._1))
          .sortBy(_._2)(ord)
          .take(10)
          .map {
            case(page, score) => Map("page" -> page, "score" -> score)
          })
        (Map(ID -> u), doc)
      }

    out.saveToEsWithMeta("blah/sims")
  }
}

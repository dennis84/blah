package blah.algo

import java.security.MessageDigest
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._

class SimilarityAlgo extends Algo {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    val reader = ctx.read.schema(SimilaritySchema())
    reader.json(rdd).registerTempTable("similarity")
    val events = ctx.sql("""|SELECT
                            |  props.user AS user,
                            |  props.item AS item
                            |FROM similarity""".stripMargin)
      .filter("user is not null and item is not null")
      .map(SimilarityEvent(_))
    require(!events.isEmpty, "view events cannot be empty")

    val usersRDD = events.groupBy(_.user)
    val users = usersRDD.keys.collect.toList.flatten

    val itemsRDD = events.groupBy(_.item)
    val items = itemsRDD.keys.collect.toList.flatten

    val views = events
      .collect { case SimilarityEvent(Some(user), Some(item)) =>
        (users.indexOf(user), items.indexOf(item))
      }

    val rows = views.map { case (user, item) =>
      (user, (item, 5.0))
    }.groupByKey().map { case (user, itemsByUser) =>
      val xs = itemsByUser.groupBy(_._1).map { case(i, group) =>
        val r = group.reduce((a, b) => a)
        (i, r._2)
      }.toArray
      Vectors.sparse(items.length, xs.map(_._1), xs.map(_._2))
    }

    val mat = new RowMatrix(rows)
    val sim = mat.columnSimilarities(0.1)

    val all = sim.toIndexedRowMatrix.rows.flatMap { case IndexedRow(i,v) =>
      val vector = v.asInstanceOf[SparseVector]
      vector.indices.zip(vector.values).flatMap { case (j,s) =>
        Seq((i.toInt,(j,s)), (j,(i.toInt,s)))
      }
    }.groupByKey.collectAsMap

    val ord = Ordering[Double].reverse

    usersRDD
      .collect { case(Some(u), elems) =>
        val doc = Similarity(u, elems.flatMap {
          case SimilarityEvent(Some(user), Some(item)) => {
            all.get(items.indexOf(item)) getOrElse Nil
          }
        }
          .map(x => (items(x._1), x._2))
          .filterNot(x => {
            elems.find(y => y.item.get == x._1).isDefined
          })
          .groupBy(_._1)
          .mapValues(x => x.max)
          .values
          .toList
          .sortBy(_._2)(ord)
          .take(10)
          .collect {
            case(item, score) => SimilarityItem(item, score)
          })

        (u, doc)
      }
  }
}

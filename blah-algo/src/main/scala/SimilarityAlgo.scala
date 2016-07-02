package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._

class SimilarityAlgo extends Algo[Similarity] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
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

    val itemsByUser = usersRDD collect { case(Some(user), events) =>
      (users.indexOf(user), events collect {
        case SimilarityEvent(_, Some(item)) => items.indexOf(item)
      })
    }

    val rows = itemsByUser map { case(_, indices) =>
      val distinctIndices = indices.toArray.distinct
      Vectors.sparse(
        items.length,
        distinctIndices,
        distinctIndices.map(_ => 5.0))
    }

    val mat = new RowMatrix(rows)
    val sim = mat.columnSimilarities(0.1)

    val data = sim.toIndexedRowMatrix.rows.flatMap {
      case IndexedRow(i, SparseVector(length, indices, values)) =>
        indices.zip(values).flatMap { case (j,s) =>
          Seq((i.toInt,(j,s)), (j,(i.toInt,s)))
        }
    }.groupByKey.collectAsMap

    val ord = Ordering[Double].reverse

    itemsRDD
      .collect { case(Some(item), events) =>
        val sims = data.get(items indexOf item) map { xs =>
          xs.toList
            .sortBy(_._2)(ord)
            .take(10)
            .map(x => (SimilarityItem(items(x._1), x._2)))
        } getOrElse Nil

        (item, Similarity(item, sims))
      }
  }
}

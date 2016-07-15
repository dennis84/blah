package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._
import blah.core.FindOpt._

class SimilarityAlgo extends Algo[Similarity] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._

    val collection = args opt "collection"
    val where = collection map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    val reader = ctx.read.schema(SimilaritySchema())
    reader.json(rdd).registerTempTable("similarity")
    val events = ctx.sql(s"""|SELECT
                             |  collection AS coll,
                             |  props.user AS user,
                             |  props.item AS item
                             |FROM similarity $where""".stripMargin)
      .filter("user is not null and item is not null")
      .map(SimilarityEvent(_))
    require(!events.isEmpty, "view events cannot be empty")

    val usersRDD = events.groupBy(_.user)
    val users = usersRDD.keys.collect.toList.flatten

    val itemsRDD = events.groupBy(_.item)
    val items = itemsRDD.keys.collect.toList.flatten

    val itemsByUser = usersRDD collect { case(Some(user), events) =>
      (users.indexOf(user), events collect {
        case SimilarityEvent(_, _, Some(item)) => items.indexOf(item)
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

        val uuid = UUID.nameUUIDFromBytes(ByteBuffer
          .allocate(Integer.SIZE / 8)
          .putInt((item + collection.getOrElse("")).hashCode)
          .array)
        (uuid.toString, Similarity(item, collection, sims))
      }
  }
}

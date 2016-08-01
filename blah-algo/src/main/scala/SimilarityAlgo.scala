package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._
import blah.core.FindOpt._

class SimilarityAlgo extends Algo[Similarity] {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._

    val collection = args opt "collection"
    val where = collection map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    val reader = ctx.read.schema(SimilaritySchema())
    reader.json(rdd).createOrReplaceTempView("similarity")
    val events = ctx.sql(s"""|SELECT
                             |  collection,
                             |  props.user AS user,
                             |  props.item AS item
                             |FROM similarity $where""".stripMargin)
      .filter("user is not null and item is not null")
      .as[SimilarityEvent]
    require(events.count > 0, "view events cannot be empty")

    val usersRdd = events.rdd.groupBy(_.user)
    val users = usersRdd.keys.collect.toList

    val itemsRdd = events.rdd.groupBy(_.item)
    val items = itemsRdd.keys.collect.toList

    val rows = usersRdd map { case(user, events) =>
      val indices = events.collect {
        case SimilarityEvent(_, _, item) => items.indexOf(item)
      }.toArray.distinct

      Vectors.sparse(
        items.length,
        indices,
        indices.map(_ => 5.0))
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

    events.groupByKey(_.item) mapGroups { case(item, events) =>
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
      Similarity(uuid.toString, item, collection, sims)
    }
  }
}

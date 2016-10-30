package blah.algo

import java.util.UUID
import java.nio.ByteBuffer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._
import FindOpt._

class RecommendationAlgo extends Algo[Recommendation] {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._

    var collection = args opt "collection"
    val where = collection map { coll =>
      s"""WHERE collection = "$coll""""
    } getOrElse ""

    val reader = ctx.read.schema(RecommendationSchema())
    reader.json(rdd).createOrReplaceTempView("recommendation")
    val events = ctx.sql(s"""|SELECT
                             |  collection,
                             |  props.user AS user,
                             |  props.item AS item
                             |FROM recommendation $where""".stripMargin)
      .filter("user is not null and item is not null")
      .as[RecommendationEvent]
    require(events.count > 0, "view events cannot be empty")

    val usersRDD = events.rdd.groupBy(_.user)
    val users = usersRDD.keys.collect.toList

    val itemsRDD = events.rdd.groupBy(_.item)
    val items = itemsRDD.keys.collect.toList

    val rows = usersRDD map { case(_, events) =>
      val indices = events.map(x => items.indexOf(x.item)).toArray.distinct
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

    events.groupByKey(_.user) mapGroups { case(user, xs) =>
      val itemsByUser = xs.toList
      val elems = itemsByUser.flatMap { event =>
        data get (items indexOf event.item) getOrElse Nil
      }

      val filtered = elems
        .map(x => (items(x._1), x._2))
        .filterNot(x => itemsByUser.find(y => y.item == x._1).isDefined)
        .groupBy(_._1)
        .mapValues(x => x.max)
        .values
        .toList
        .sortBy(_._2)(ord)
        .take(10)
        .collect {
          case(item, score) => RecommendationItem(item, score)
        }

      val uuid = UUID.nameUUIDFromBytes(ByteBuffer
        .allocate(Integer.SIZE / 8)
        .putInt((user + collection.getOrElse("")).hashCode)
        .array)
      Recommendation(uuid.toString, user, collection, filtered)
    }
  }
}

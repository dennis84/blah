package blah.algo

import java.util.UUID
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import blah.core.FindOpt._

class MostViewedAlgo extends Algo[MostViewed] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
    val config = MostViewedConfig(
      args opt "max" map (_.toInt) getOrElse 100,
      args opt "from")

    val reader = ctx.read.schema(MostViewedSchema())
    reader.json(rdd).registerTempTable("most_viewed")
    val events = ctx.sql("""|SELECT
                            |  date,
                            |  collection,
                            |  props.item AS item
                            |FROM most_viewed""".stripMargin)
      .filter("item is not null")
      .map(MostViewedEvent(_))
    require(!events.isEmpty, "events cannot be empty")

    events
      .map(x => ((x.item.get, x.collection), 1))
      .reduceByKey(_ + _)
      .groupBy(_._1._2)
      .map { case(collection, xs) =>
        val items = xs.toList
          .sortBy(_._2)(Ordering[Int].reverse)
          .take(config.max)
          .zipWithIndex
          .map { case(((item, collection), count), pos) =>
            MostViewedItem(item, pos.toInt, count)
          }
        (collection, MostViewed(collection, items))
      }
  }
}

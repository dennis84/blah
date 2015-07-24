package blah.similarity

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.{Vectors, SparseVector}
import org.apache.spark.mllib.linalg.distributed._

object Worker extends App {

  val sc = new SparkContext("local[*]", "test")

  val views = Seq(
    (1, 100),
    (1, 100),
    (2, 100),
    (2, 101),
    (2, 102),
    (3, 102),
    (4, 103),
    (5, 103),
    (5, 104),
    (2, 104),
    (6, 105)
  )

  val trainingSet = sc.parallelize(views)

  val rows = trainingSet.map { case (u, i) =>
    (u, (i, 1.0))
  }.groupByKey().map { case (u, ir) =>
    val irDedup: Map[Int, Double] = ir
      .groupBy(_._1)
      .map { case (i, irGroup) =>
        val r = irGroup.reduce((a, b) => a)
        (i, r._2)
      }

    val irSorted = irDedup.toArray.sortBy(_._1)
    val indexes = irSorted.map(_._1)
    val values = irSorted.map(_._2)
    Vectors.sparse(106, indexes, values)
  }

  val mat = new RowMatrix(rows)
  val sim = mat.columnSimilarities(0.5)

  val all = sim.toIndexedRowMatrix.rows.flatMap { case IndexedRow(i,v) =>
    val vector = v.asInstanceOf[SparseVector]
    vector.indices.zip(vector.values).flatMap { case (j,s) =>
      Seq((i,(j,s)), (j,(i,s)))
    }
  }

  val ord = Ordering[Double].reverse

  for (i <- 100 to 105) {
    println(all.lookup(i).sortBy(_._2)(ord))
  }

  sc.stop()
}

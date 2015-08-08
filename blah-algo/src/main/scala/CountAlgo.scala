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

class CountAlgo extends Algo {
  def train {
    val conf = new SparkConf()
      .setAppName("count")
      .set("spark.cassandra.connection.host", "127.0.0.1")
    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://localhost:9000/user/dennis/blah/events/*")

    val events = rdd
      .map(x => Try(x.parseJson.convertTo[ViewEvent]))
      .filter(_.isSuccess)
      .map(_.get)
      .map(x => ((x.props.event, x.date.withTimeAtStartOfDay), 1))
      .reduceByKey(_ + _)
      .map(x => (x._1._1, x._1._2, x._2))
    
    events.saveToCassandra("blah", "count", SomeColumns("name", "date", "count"))
    sc.stop
  }
}

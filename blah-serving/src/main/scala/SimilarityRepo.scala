package blah.serving

import scala.concurrent._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.datastax.driver.core.Session
import blah.core.CassandraTweaks

class SimilarityRepo(
  conn: Session
)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def sims(q: SimilarityQuery): Future[SimilarityResult] = {
    val cql = s"""|select * from blah.sims
                  |where user='${q.user}'
                  |;""".stripMargin
    conn.executeAsync(cql) map { res =>
      val views: Map[String, Double] =
        if(!res.isExhausted)
          res.one.getMap("views", classOf[String], classOf[java.lang.Double])
            .asScala.toMap.map(x => x._1 -> x._2.doubleValue)
        else Map.empty
      SimilarityResult(q.user,
          q.limit map (x => views take x.toInt) getOrElse views)
    }
  }
}

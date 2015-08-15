package blah.serving

import scala.concurrent._
import scala.collection.JavaConversions._
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
      val views = if(!res.isExhausted)
                    res.one.getList("views", classOf[String]).toList
                  else Nil
      SimilarityResult(q.user,
          q.limit map (x => views take x.toInt) getOrElse views)
    }
  }
}

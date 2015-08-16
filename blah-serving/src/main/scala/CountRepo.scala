package blah.serving

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import blah.core.CassandraTweaks

class CountRepo(
  conn: Session
)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def count(q: CountQuery): Future[CountResult] = {
    val where = q.event map (x => s"where name='$x'") getOrElse ""
    val from = q.from map (x => s"and date >= '${x.getMillis}'") getOrElse ""
    val to = q.to map (x => s"and date <= '${x.getMillis}'") getOrElse ""
    val cql = s"""|select count from blah.count
                  |$where
                  |$from
                  |$to
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      CountResult(xs.all.map(_.getLong("count")).sum)
    }
  }

  def countAll(q: CountAllQuery): Future[CountAllResult] = {
    val from = q.from map (x => s"and date >= '${x.getMillis}'") getOrElse ""
    val to = q.to map (x => s"and date <= '${x.getMillis}'") getOrElse ""
    val cql = s"""|select name, count from blah.count
                  |$from
                  |$to
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      val ys = xs.map(x => (x.getString("name"), x.getLong("count")))
      val zs = ys.groupBy(_._1).map(x => PageView(x._1, x._2.map(_._2).sum)).toList
      CountAllResult(zs)
    }
  }
}

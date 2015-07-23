package blah.count

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import blah.core.CassandraTweaks

class Repo(conn: Session)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def count(q: CountQuery): Future[CountResult] = {
    val from = q.from map (x => s"and date >= '${x.getMillis}'") getOrElse ""
    val to = q.to map (x => s"and date <= '${x.getMillis}'") getOrElse ""
    val cql = s"""|select count from blah.count
                  |where name='${q.event}'
                  |$from
                  |$to
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      CountResult(xs.all.map(_.getLong("count")).sum)
    }
  }
}

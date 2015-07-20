package blah.example

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import com.github.nscala_time.time.Imports._
import blah.core.CassandraTweaks

class Repo(conn: Session)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def count(q: CountQuery): Future[CountResult] = {
    val from = q.from getOrElse "0000-00-00 00:00"
    val to = DateTime.now.toString
    val cql = s"""|select count from blah.count
                  |where name='${q.event}'
                  |and date >= '$from'
                  |and date <= '$to'
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      CountResult(xs.all.map(_.getLong("count")).sum)
    }
  }
}

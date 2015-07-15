package blah.example

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import blah.core.CassandraTweaks

class Repo(conn: Session)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def count(q: CountQuery): Future[CountResult] = {
    val cql = s"""|select count from blah.count
                  |where name='${q.event}';
                  |""".stripMargin
    conn.executeAsync(cql) map { xs =>
      CountResult(xs.all.map(_.getInt("count")).sum)
    }
  }
}

package blah.serving

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import blah.core.CassandraTweaks

class CountRepo(
  conn: Session
)(implicit ec: ExecutionContext) extends CassandraTweaks {

  def query(q: Query) =
    conn.executeAsync(QueryToCQL(q)) map { xs =>
      CountResult(xs.all.map(_.getLong("count")).sum)
    }

  def count(q: CountQuery): Future[CountResult] = {
    val where = List(
      q.page.map(x => s"name='$x'"),
      q.from.map(x => s"date >= '${x.getMillis}'"),
      q.to.map(x => s"date <= '${x.getMillis}'")
    ).flatten.foldLeft("") {
      case ("", x) => "where " + x
      case (a, x)  => a + " and " + x
    }
    val cql = s"""|select count from blah.count
                  |$where allow filtering
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      CountResult(xs.all.map(_.getLong("count")).sum)
    }
  }

  def countAll(q: CountAllQuery): Future[CountAllResult] = {
    val where = List(
      q.from.map(x => s"date >= '${x.getMillis}'"),
      q.to.map(x => s"date <= '${x.getMillis}'")
    ).flatten.foldLeft("") {
      case ("", x) => "where " + x
      case (a, x)  => a + " and " + x
    }
    val cql = s"""|select name, count from blah.count
                  |$where allow filtering
                  |;""".stripMargin
    conn.executeAsync(cql) map { xs =>
      val ys = xs.map(x => (x.getString("name"), x.getLong("count")))
      val zs = ys.groupBy(_._1).map(x => PageView(x._1, x._2.map(_._2).sum)).toList
      CountAllResult(zs)
    }
  }
}

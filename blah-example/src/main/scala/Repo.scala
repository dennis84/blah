package blah.example

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.{Session, Row}
import com.datastax.driver.core.querybuilder.QueryBuilder
import blah.core.CassandraTweaks

class Repo(conn: Session) extends CassandraTweaks {

  def findAll(implicit ec: ExecutionContext): Future[List[Example]] = {
    val query = QueryBuilder.select().all().from("blah", "example")
    conn.executeAsync(query) map (_.all().map(mkView).toList)
  }

  private def mkView(r: Row) = Example(
    r.getString("name"),
    r.getLong("count"))
}

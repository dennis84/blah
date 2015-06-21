package blah.api

import com.datastax.driver.core.Cluster

trait SetUp {

  def withSchema(cluster: Cluster)(fn: => Unit) {
    val session = cluster.connect

    session.execute(
      """|CREATE KEYSPACE IF NOT EXISTS blah
         |WITH REPLICATION = {
         | 'class': 'SimpleStrategy',
         | 'replication_factor': 1
         |};""".stripMargin)

    session.execute(
      """|CREATE TABLE IF NOT EXISTS blah.events (
         | id text PRIMARY KEY,
         | name text,
         | props map<text, text>
         |);""".stripMargin)

    session.close
    fn
  }
}

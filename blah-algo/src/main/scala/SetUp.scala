package blah.algo

import blah.core.DefaultCassandraCluster

object SetUp {
  def main(args: Array[String]) {
    val cluster = DefaultCassandraCluster()
    val session = cluster.connect
    session.execute(
      """|CREATE KEYSPACE IF NOT EXISTS blah
         |WITH REPLICATION = {
         | 'class': 'SimpleStrategy',
         | 'replication_factor': 1
         |};""".stripMargin)
    session.execute(
      """|CREATE TABLE IF NOT EXISTS blah.sims (
         | user text,
         | views map<text, double>,
         | PRIMARY KEY ((user))
         |);""".stripMargin)
    session.execute(
      """|CREATE TABLE IF NOT EXISTS blah.count (
         | name text,
         | date timestamp,
         | count bigint,
         | browser_family text,
         | browser_major text,
         | browser_minor text,
         | os_family text,
         | os_major text,
         | os_minor text,
         | device_family text,
         | PRIMARY KEY (
         |  (name), date, browser_family, browser_major, browser_minor,
         |  os_family, os_major, os_minor, device_family)
         |);""".stripMargin)
    session.close
  }
}

package blah.core

import scala.collection.JavaConversions._
import com.datastax.driver.core.{ProtocolOptions, Cluster}

object DefaultCassandraCluster {
  def apply() = Cluster.builder()
    .addContactPoints(List("127.0.0.1"): _*)
    .withCompression(ProtocolOptions.Compression.SNAPPY)
    .withPort(9042)
    .build()
}

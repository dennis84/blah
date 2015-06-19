package blah

import akka.actor._
import com.datastax.driver.core.{ProtocolOptions, Cluster}

class Env(system: ActorSystem) {
  val api = system.actorOf(Props[Api])

  import scala.collection.JavaConversions._
  lazy val cluster = Cluster.builder()
    .addContactPoints(List("127.0.0.1"): _*)
    .withCompression(ProtocolOptions.Compression.SNAPPY)
    .withPort(9042)
    .build()

  lazy val conn = cluster.connect("blah")
}

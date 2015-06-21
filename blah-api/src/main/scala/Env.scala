package blah.api

import akka.actor._
import com.datastax.driver.core.{ProtocolOptions, Cluster}
import blah.core._

class Env(system: ActorSystem) {
  import scala.collection.JavaConversions._
  lazy val cluster = Cluster.builder()
    .addContactPoints(List("127.0.0.1"): _*)
    .withCompression(ProtocolOptions.Compression.SNAPPY)
    .withPort(9042)
    .build()

  lazy val conn = cluster.connect("blah")
  lazy val repo = new EventRepo(conn)
  lazy val api = system.actorOf(Props(new Api(repo)))
}

package blah.serving

import akka.actor._
import blah.core._

class Env(system: ActorSystem) {
  lazy val cluster = DefaultCassandraCluster()
  lazy val conn = cluster.connect("blah")
  lazy val repo = new EventRepo(conn)
  lazy val api = system.actorOf(Props(new Api(repo)))
}

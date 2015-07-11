package blah.serving

import akka.actor.ActorSystem
import blah.core._

class Env(system: ActorSystem) {
  lazy val cluster = DefaultCassandraCluster()
  lazy val conn = cluster.connect("blah")
}

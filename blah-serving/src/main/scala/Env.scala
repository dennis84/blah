package blah.serving

import akka.actor.ActorSystem
import blah.core.DefaultCassandraCluster

class Env(val system: ActorSystem) {
  private lazy val cluster = DefaultCassandraCluster()
  lazy val cassandraConnection = cluster.connect("blah")
  lazy val websocket = new WebsocketHub(system)
}

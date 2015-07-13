package blah.core

import akka.actor.ActorSystem

class ServingEnv(system: ActorSystem) {
  private lazy val cluster = DefaultCassandraCluster()
  lazy val cassandraConnection = cluster.connect("blah")
  lazy val websocket = new WebsocketHub(system)
}

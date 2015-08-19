package blah.serving

import akka.actor.ActorSystem
import kafka.serializer.StringDecoder
import com.softwaremill.react.kafka.{ReactiveKafka, ConsumerProperties}
import blah.core.DefaultCassandraCluster

class Env(val system: ActorSystem) {
  implicit val s = system
  private val config = system.settings.config
  private lazy val cluster = DefaultCassandraCluster()
  lazy val cassandraConnection = cluster.connect("blah")
  lazy val websocket = new WebsocketHub(system)

  lazy val kafka = new ReactiveKafka()
  lazy val consumer = kafka.consume(ConsumerProperties(
    brokerList = config.getString("consumer.broker.list"),
    zooKeeperHost = config.getString("consumer.zookeeper.connect"),
    topic = "trainings",
    groupId = "websocket",
    decoder = new StringDecoder()
  ))
}

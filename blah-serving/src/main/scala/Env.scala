package blah.serving

import akka.actor.{ActorSystem, Props}
import kafka.serializer.StringDecoder
import com.softwaremill.react.kafka.{ReactiveKafka, ConsumerProperties}

class Env(val system: ActorSystem) {
  implicit val s = system
  private val config = system.settings.config
  lazy val websocketRoom = new WebsocketRoom(system)
  lazy val websocketHub = system.actorOf(Props(new WebsocketHub(websocketRoom)))

  lazy val kafka = new ReactiveKafka()
  lazy val consumer = kafka.consume(ConsumerProperties(
    brokerList = config.getString("consumer.broker.list"),
    zooKeeperHost = config.getString("consumer.zookeeper.connect"),
    topic = "trainings",
    groupId = "websocket",
    decoder = new StringDecoder()
  ))
}

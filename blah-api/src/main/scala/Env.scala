package blah.api

import akka.actor._
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder
import com.softwaremill.react.kafka.ProducerProperties
import com.typesafe.config.ConfigFactory

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config
  lazy val producer = KafkaProducer[String](ProducerProperties(
    brokerList = config.getString("producer.broker.list"),
    topic = "events_2",
    clientId = "events",
    encoder = new StringEncoder()
  ))
  lazy val api = system.actorOf(Props(new EventApi(producer)))
}

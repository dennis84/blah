package blah.api

import akka.actor.ActorSystem
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder
import com.softwaremill.react.kafka.ProducerProperties

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config
  lazy val producer = KafkaProducer[String](ProducerProperties(
    brokerList = config.getString("producer.broker.list"),
    topic = "events",
    clientId = "events",
    encoder = new StringEncoder
  ))
}

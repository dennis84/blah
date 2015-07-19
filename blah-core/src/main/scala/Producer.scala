package blah.core

import java.util.Properties
import kafka.producer.{ProducerConfig, KeyedMessage, Producer => KafkaProducer}
import com.typesafe.config.ConfigFactory

case class Producer[A](props: Properties, topic: String) {
  private val config = new ProducerConfig(props)
  private lazy val producer = new KafkaProducer[A, A](config)

  def send(message: A) =
    producer.send(new KeyedMessage[A, A](topic, message))
}

object Producer {
  def apply[A](topic: String): Producer[A] = {
    val conf = ConfigFactory.load()
    val props = new Properties
    props.put("metadata.broker.list", conf.getString("producer.metadata.broker.list"))
    props.put("serializer.class", conf.getString("producer.serializer.class"))
    Producer(props, topic)
  }
}

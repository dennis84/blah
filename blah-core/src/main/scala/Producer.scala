package blah.core

import java.util.Properties
import kafka.producer.{ProducerConfig, KeyedMessage, Producer => KafkaProducer}
import spray.json._

trait ProducerProps extends Properties {
  put("metadata.broker.list", "127.0.0.1:9092")
  put("serializer.class", "kafka.serializer.StringEncoder")
}

object ProducerProps {
  def apply() = new ProducerProps {}
}

case class Producer[A](topic: String) {
  val config = new ProducerConfig(ProducerProps())
  private lazy val producer = new KafkaProducer[A, A](config)

  def send(message: A)(implicit writer: JsonWriter[A]) =
    producer.send(new KeyedMessage[A, A](topic, message))
}

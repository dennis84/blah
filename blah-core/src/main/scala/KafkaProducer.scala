package blah.core

import com.softwaremill.react.kafka.ProducerProperties
import org.apache.kafka.clients.producer.{KafkaProducer => Producer}
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducer[K,V](topic: String, producer: Producer[K,V]) {
  def send(message: V): Unit =
    producer.send(new ProducerRecord(topic, message))
}

object KafkaProducer {
  def apply[K,V](props: ProducerProperties[K,V]): KafkaProducer[K,V] =
    new KafkaProducer(props.topic, new Producer(
      props.rawProperties,
      props.keySerializer,
      props.valueSerializer))
}

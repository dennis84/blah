package blah.core

import java.util.concurrent.TimeoutException
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.softwaremill.react.kafka.ProducerProperties
import org.apache.kafka.clients.producer.{KafkaProducer => Producer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

class KafkaProducer[K,V](topic: String, producer: Producer[K,V]) {
  def send(message: V)(implicit ec: ExecutionContext): Future[RecordMetadata] =
    Future(producer.send(new ProducerRecord(topic, message))).map(_.get)
}

object KafkaProducer {
  def apply[K,V](props: ProducerProperties[K,V]): KafkaProducer[K,V] =
    new KafkaProducer(props.topic, new Producer(
      props.rawProperties,
      props.keySerializer,
      props.valueSerializer))
}

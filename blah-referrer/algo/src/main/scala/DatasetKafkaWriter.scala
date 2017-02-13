package blah.referrer

import java.util.Properties
import scala.reflect.ClassTag
import scala.language.implicitConversions
import org.apache.spark.sql.Dataset
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class DatasetKafkaWriter[T: ClassTag](ds: Dataset[T]) extends Serializable {
  def writeToKafka[K, V](props: Properties, fn: T => ProducerRecord[K, V]) =
    ds foreachPartition { events =>
      val producer = new KafkaProducer[K, V](props)
      (events map fn) foreach producer.send
    }
}

object DatasetKafkaWriter {
  implicit def kafkaRdd[T: ClassTag](ds: Dataset[T]) =
    new DatasetKafkaWriter(ds)
}

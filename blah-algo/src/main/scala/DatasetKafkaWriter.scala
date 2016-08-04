package blah.algo

import java.util.Properties
import scala.reflect.ClassTag
import scala.language.implicitConversions
import org.apache.spark.sql.Dataset
import kafka.producer.{ProducerConfig, Producer, KeyedMessage}

class DatasetKafkaWriter[T: ClassTag](ds: Dataset[T]) extends Serializable {
  def writeToKafka[K, V](props: Properties, fn: T => KeyedMessage[K, V]) =
    ds foreachPartition { events =>
      val producer = new Producer[K, V](new ProducerConfig(props))
      producer.send((events map fn).toArray: _*)
    }
}

object DatasetKafkaWriter {
  implicit def kafkaRdd[T: ClassTag](ds: Dataset[T]) =
    new DatasetKafkaWriter(ds)
}

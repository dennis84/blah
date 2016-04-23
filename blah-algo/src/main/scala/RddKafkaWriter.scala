package blah.algo

import java.util.Properties
import scala.reflect.ClassTag
import org.apache.spark.rdd.RDD
import kafka.producer.{ProducerConfig, Producer, KeyedMessage}

class RddKafkaWriter[T: ClassTag](rdd: RDD[T]) extends Serializable {
  def writeToKafka[K, V](props: Properties, fn: T => KeyedMessage[K, V]) =
    rdd foreachPartition { events =>
      val producer = new Producer[K, V](new ProducerConfig(props))
      producer.send((events map fn).toArray: _*)
    }
}

object RddKafkaWriter {
  implicit def kafkaRdd[T: ClassTag](rdd: RDD[T]) = new RddKafkaWriter(rdd)
}

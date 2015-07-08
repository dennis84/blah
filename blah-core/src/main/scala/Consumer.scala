package blah.core

import java.util.Properties
import scala.collection.JavaConversions._
import kafka.consumer.{Consumer => KafkaConsumer}
import kafka.consumer._
import kafka.serializer._
import kafka.api._

trait ConsumerProps extends Properties {
  put("group.id", "1234")
  put("zookeeper.connect", "localhost:2181")
  //put("auto.offset.reset", "smallest")
}

object ConsumerProps {
  def apply() = new ConsumerProps {}
}

class Consumer(topic: String) {
  private val config = new ConsumerConfig(ConsumerProps())
  private val connector = KafkaConsumer.create(config)
  private val stream = connector.createMessageStreamsByFilter(
    topicFilter = new Whitelist(topic),
    numStreams = 1,
    keyDecoder = new DefaultDecoder(),
    valueDecoder = new DefaultDecoder()
  ).get(0)

  def read(): Stream[String] =
    Stream.cons(new String(stream.head.message), read)
}

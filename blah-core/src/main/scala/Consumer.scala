package blah.core

import java.util.Properties
import scala.collection.JavaConversions._
import com.typesafe.config.ConfigFactory
import kafka.consumer.{Consumer => KafkaConsumer}
import kafka.consumer._
import kafka.serializer._
import kafka.api._

class Consumer(props: Properties, topic: String) {
  private val config = new ConsumerConfig(props)
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

object Consumer {
  def apply(topic: String): Consumer = {
    val conf = ConfigFactory.load()
    val props = new Properties
    props.put("zookeeper.connect", conf.getString("consumer.zookeeper.connect"))
    props.put("group.id", conf.getString("consumer.group.id"))
    new Consumer(props, topic)
  }
}

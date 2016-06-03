package blah.api

import java.net.URI
import akka.actor._
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder
import com.softwaremill.react.kafka.ProducerProperties
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import blah.core.{HdfsWriter, HdfsWriterConfig, KafkaProducerActor}

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config

  lazy val producer = system.actorOf(Props(
    new KafkaProducerActor(KafkaProducer(ProducerProperties(
      brokerList = config.getString("producer.broker.list"),
      topic = "events",
      clientId = "events",
      encoder = new StringEncoder
    )))))

  private lazy val dfs = FileSystem.get(
    URI.create(config.getString("hadoop.url")),
    new Configuration)

  lazy val hdfs = system.actorOf(Props(
    new HdfsWriter(dfs, HdfsWriterConfig())))
}

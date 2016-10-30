package blah.api

import java.net.URI
import akka.actor._
import akka.kafka._
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config

  lazy val producerSettings = ProducerSettings(system,
    new StringSerializer, new StringSerializer)
    .withBootstrapServers(config.getString("producer.broker.list"))

  lazy val producer = system.actorOf(Props(new KafkaProducerActor(
    producerSettings.createKafkaProducer(), "events"
  )))

  private lazy val dfs = FileSystem.get(
    URI.create(config.getString("hadoop.url")),
    new Configuration)

  lazy val hdfs = system.actorOf(Props(
    new HdfsWriter(dfs, HdfsWriterConfig())))
}

package blah.api

import java.net.URI
import akka.actor._
import org.apache.kafka.common.serialization.StringSerializer
import com.softwaremill.react.kafka.ProducerProperties
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import blah.core.KafkaProducer

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config
  lazy val producer = KafkaProducer(ProducerProperties(
    bootstrapServers = config.getString("producer.broker.list"),
    valueSerializer = new StringSerializer,
    topic = "events"))

  private val dfs = FileSystem.get(
    URI.create(config.getString("hadoop.url")),
    new Configuration)

  lazy val hdfs = system.actorOf(Props(
    new HdfsWriter(dfs, HdfsWriterConfig())))
}

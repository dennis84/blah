package blah.api

import akka.actor._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import blah.core._

class Env(system: ActorSystem) {
  import system.dispatcher
  private val config = system.settings.config
  private val hadoopConf = new Configuration
  hadoopConf.addResource(new Path(s"${config.getString("hadoop.conf_dir")}/core-site.xml"))
  hadoopConf.addResource(new Path(s"${config.getString("hadoop.conf_dir")}/hdfs-site.xml"))
  private val hadoopFs = FileSystem.get(hadoopConf)
  lazy val eventRepo = new EventRepo(hadoopFs)
  lazy val producer = Producer[String]("events_2")
  lazy val api = system.actorOf(Props(new EventApi(eventRepo, producer)))
}

package blah.api

import java.util.UUID
import scala.concurrent.duration._
import org.scalatest._
import akka.actor._
import akka.testkit._
import spray.json._
import com.github.nscala_time.time.Imports._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import blah.core._

class StorageSpec
  extends TestKit(ActorSystem("test"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfter {

  def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val dfsConf = new Configuration()
  dfsConf.addResource(new Path("/usr/local/Cellar/hadoop/2.7.1/libexec/etc/hadoop/core-site.xml"));
  dfsConf.addResource(new Path("/usr/local/Cellar/hadoop/2.7.1/libexec/etc/hadoop/hdfs-site.xml"));
  val dfs = FileSystem.get(dfsConf)
  val storage = system.actorOf(Props(new Storage(dfs, StorageConfig())))

  "The Storage" should "works" in {
    storage ! Storage.Write(Event(
      UUID.randomUUID.toString, "view",
      DateTime.now))
    expectNoMsg(DurationInt(2).seconds)

    storage ! Storage.Write(Event(
      UUID.randomUUID.toString, "view",
      DateTime.now))
    expectNoMsg(DurationInt(2).seconds)

    storage ! Storage.Close
    expectNoMsg(DurationInt(2).seconds)

    storage ! Storage.Write(Event(
      UUID.randomUUID.toString, "view",
      DateTime.now))
    expectNoMsg(DurationInt(2).seconds)

    storage ! Storage.Close
    expectNoMsg(DurationInt(2).seconds)
  }
}

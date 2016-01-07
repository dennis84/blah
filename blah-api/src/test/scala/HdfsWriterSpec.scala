package blah.api

import java.util.UUID
import scala.concurrent.duration._
import org.scalatest._
import akka.actor._
import akka.testkit._
import akka.util.ByteString
import spray.json._
import com.github.nscala_time.time.Imports._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, LongWritable, BytesWritable}
import blah.core._
import JsonProtocol._

class HdfsWriterSpec
  extends TestKit(ActorSystem("test"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfter {

  val dfsConf = new Configuration()
  dfsConf.set("fs.file.impl", "org.apache.hadoop.fs.RawLocalFileSystem")
  val dfs = FileSystem.get(dfsConf)
  val writer = system.actorOf(Props(new HdfsWriter(dfs, HdfsWriterConfig(
    path = "target/test/events/%Y/%m/%d"))))

  before {
    dfs.delete(new Path("target/test/events"), true)
  }

  def afterAll {
    TestKit.shutdownActorSystem(system)
    dfs.close()
  }

  "The HdfsWriter" should "write" in {
    val event1 = Event(UUID.randomUUID.toString, "view", DateTime.now)
    val event2 = Event(UUID.randomUUID.toString, "view", DateTime.now)
    val event3 = Event(UUID.randomUUID.toString, "view", DateTime.now)

    writer ! HdfsWriter.Write(event1)
    expectNoMsg(DurationInt(2).seconds)

    writer ! HdfsWriter.Write(event2)
    expectNoMsg(DurationInt(2).seconds)

    writer ! HdfsWriter.Close
    expectNoMsg(DurationInt(2).seconds)

    writer ! HdfsWriter.Write(event3)
    expectNoMsg(DurationInt(2).seconds)

    writer ! HdfsWriter.Close
    expectNoMsg(DurationInt(2).seconds)

    val statuses = dfs.globStatus(new Path("target/test/events/*/*/*/*"))
    statuses.length should be (2)

    var reader = new SequenceFile.Reader(dfsConf,
      SequenceFile.Reader.file(statuses(0).getPath))

    var key = new LongWritable
    var value = new BytesWritable

    reader.next(key, value)
    ByteString(value.copyBytes)
      .utf8String
      .parseJson
      .convertTo[Event] should be (event1)

    reader.next(key, value)
    ByteString(value.copyBytes)
      .utf8String
      .parseJson
      .convertTo[Event] should be (event2)

    reader = new SequenceFile.Reader(dfsConf,
      SequenceFile.Reader.file(statuses(1).getPath))

    reader.next(key, value)
    ByteString(value.copyBytes)
      .utf8String
      .parseJson
      .convertTo[Event] should be (event3)
  }

  it should "close stream after 10 seconds" in {
    val event1 = Event(UUID.randomUUID.toString, "view", DateTime.now)
    writer ! HdfsWriter.Write(event1)
    Thread sleep 10000

    val statuses = dfs.globStatus(new Path("target/test/events/*/*/*/*"))
    statuses.length should be (1)

    var reader = new SequenceFile.Reader(dfsConf,
      SequenceFile.Reader.file(statuses(0).getPath))

    var key = new LongWritable
    var value = new BytesWritable

    reader.next(key, value)
    ByteString(value.copyBytes)
      .utf8String
      .parseJson
      .convertTo[Event] should be (event1)
  }
}

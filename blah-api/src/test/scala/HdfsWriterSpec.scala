package blah.api

import java.util.UUID
import scala.concurrent.duration._
import org.scalatest._
import akka.actor._
import akka.testkit._
import akka.util.ByteString
import spray.json._
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

  before {
    dfs.delete(new Path("target/test/events"), true)
  }

  def afterAll {
    TestKit.shutdownActorSystem(system)
    dfs.close()
  }

  "The HdfsWriter" should "write" in {
    val writer = system.actorOf(Props(new HdfsWriter(dfs, HdfsWriterConfig(
      path = "target/test/events/%Y/%m/%d"))))

    val event1 = Event(UUID.randomUUID.toString, "view")
    val event2 = Event(UUID.randomUUID.toString, "view")
    val event3 = Event(UUID.randomUUID.toString, "view")

    writer ! HdfsWriter.Write(event1)
    expectNoMsg(100.milliseconds)

    writer ! HdfsWriter.Write(event2)
    expectNoMsg(100.milliseconds)

    writer ! HdfsWriter.Close
    expectNoMsg(100.milliseconds)

    writer ! HdfsWriter.Write(event3)
    expectNoMsg(100.milliseconds)

    writer ! HdfsWriter.Close
    expectNoMsg(100.milliseconds)

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

  it should "close stream after close delay" in {
    val writer = system.actorOf(Props(new HdfsWriter(dfs, HdfsWriterConfig(
      path = "target/test/events/%Y/%m/%d",
      closeDelay = 100.milliseconds))))

    val event1 = Event(UUID.randomUUID.toString, "view")
    writer ! HdfsWriter.Write(event1)
    Thread sleep 200

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

  it should "close stream when batchSize is full" in {
    val event = Event(UUID.randomUUID.toString, "view")
    val length = event.toJson.compactPrint.getBytes.length

    val writer = system.actorOf(Props(new HdfsWriter(dfs, HdfsWriterConfig(
      path = "target/test/events/%Y/%m/%d",
      batchSize = length * 2,
      closeDelay = 100.milliseconds))))

    writer ! HdfsWriter.Write(event)
    writer ! HdfsWriter.Write(event)
    writer ! HdfsWriter.Write(event)
    writer ! HdfsWriter.Write(event)

    Thread sleep 500

    val statuses = dfs.globStatus(new Path("target/test/events/*/*/*/*"))
    statuses.length should be (2)

    var reader = new SequenceFile.Reader(dfsConf,
      SequenceFile.Reader.file(statuses(0).getPath))

    var key = new LongWritable
    var value = new BytesWritable
    var nb = 0

    while(reader.next(key, value)) (nb += 1)
    nb should be (3)

    reader = new SequenceFile.Reader(dfsConf,
      SequenceFile.Reader.file(statuses(1).getPath))
    nb = 0

    while(reader.next(key, value)) (nb += 1)
    nb should be (1)
  }
}

package blah.api

import scala.concurrent.duration._
import java.text.DecimalFormat
import akka.actor._
import spray.json._
import com.github.nscala_time.time.Imports._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, LongWritable, Text, IOUtils}
import blah.core._
import JsonProtocol._

class Storage(dfs: FileSystem, config: StorageConfig) extends Actor {
  import context.dispatcher

  // context.system.scheduler.schedule(
  //   DurationInt(10).seconds,
  //   DurationInt(10).seconds,
  //   self, Storage.Close)

  def receive = active(newWriter, 0L)

  def active(writer: SequenceFile.Writer, bytesWritten: Long): Receive = {
    case Storage.Write(e) if bytesWritten > config.batchSize =>
      val key = new LongWritable(0L)
      val value = new Text(e.toJson.compactPrint)
      writer.append(key, value)
      close(writer)
      context become active(newWriter, 0L)

    case Storage.Write(e) =>
      val key = new LongWritable(0L)
      val value = new Text(e.toJson.compactPrint)
      writer.append(key, value)
      context become active(writer, bytesWritten + value.getLength)

    case Storage.Close =>
      close(writer)
      context become receive
  }

  private def close(writer: SequenceFile.Writer) {
    writer.hflush()
    IOUtils.closeStream(writer)
  }

  private def newWriter() = {
    val now = DateTime.now
    val df = new DecimalFormat("00")
    val file = Seq(config.filePrefix, now.getMillis, config.fileSuffix) mkString ""
    val path = new Path(Seq(
      "%Y" -> now.getYear.toString,
      "%m" -> df.format(now.getMonthOfYear),
      "%d" -> df.format(now.getDayOfMonth)
    ).foldLeft(config.path + "/" + file) {_.replaceAll _ tupled(_)})
    SequenceFile.createWriter(dfs.getConf,
      SequenceFile.Writer.stream(dfs.create(path)),
      SequenceFile.Writer.keyClass(classOf[LongWritable]),
      SequenceFile.Writer.valueClass(classOf[Text]))
  }
}

object Storage {
  case class Write(event: Event)
  case object Close
}

case class StorageConfig(
  path: String = "/events/%Y/%m/%d",
  filePrefix: String = "events",
  fileSuffix: String = ".jsonl",
  batchSize: Long = 1024 * 1024 * 256)

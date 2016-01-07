package blah.api

import scala.concurrent.duration._
import java.text.DecimalFormat
import akka.actor._
import spray.json._
import com.github.nscala_time.time.Imports._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{SequenceFile, LongWritable, BytesWritable, IOUtils}
import blah.core._
import JsonProtocol._

class HdfsWriter(
  dfs: FileSystem,
  config: HdfsWriterConfig
) extends Actor with ActorLogging {
  import context.dispatcher

  context.system.scheduler.schedule(
    DurationInt(10).seconds,
    DurationInt(10).seconds,
    self, HdfsWriter.Close)

  def receive = {
    case m@HdfsWriter.Write(e) =>
      context become active(newWriter, 0L)
      self ! m
  }

  def active(writer: SequenceFile.Writer, bytesWritten: Long): Receive = {
    case HdfsWriter.Write(e) =>
      log.debug("Write event")
      val value = new BytesWritable(e.toJson.compactPrint.getBytes)
      writer.append(new LongWritable(0L), value)
      if(bytesWritten > config.batchSize) {
        close(writer)
        context become active(newWriter, 0L)
      } else {
        context become active(writer, bytesWritten + value.getLength)
      }

    case HdfsWriter.Close =>
      log.debug("Close writer")
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
      SequenceFile.Writer.valueClass(classOf[BytesWritable]))
  }
}

object HdfsWriter {
  case class Write(event: Event)
  case object Close
}

case class HdfsWriterConfig(
  path: String = "/events/%Y/%m/%d",
  filePrefix: String = "events",
  fileSuffix: String = ".jsonl",
  batchSize: Long = 1024 * 1024 * 256)

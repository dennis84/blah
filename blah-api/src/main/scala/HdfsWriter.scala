package blah.api

import scala.concurrent.duration._
import java.text.DecimalFormat
import akka.actor._
import spray.json._
import com.github.nscala_time.time.Imports._
import org.apache.hadoop.fs.{FileSystem, Path, FSDataOutputStream}
import org.apache.hadoop.io.{SequenceFile, LongWritable, BytesWritable}
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
      val (writer, stream) = nextWriterAndStream
      context become active(writer, stream, 0L)
      self ! m
  }

  def active(
    writer: SequenceFile.Writer,
    stream: FSDataOutputStream,
    bytesWritten: Long
  ): Receive = {
    case HdfsWriter.Write(e) =>
      log.debug("Write event")
      val value = new BytesWritable(e.toJson.compactPrint.getBytes)
      writer.append(new LongWritable(0L), value)
      if(bytesWritten > config.batchSize) {
        writer.hflush()
        writer.close()
        stream.close()
        val (nextWriter, nextStream) = nextWriterAndStream
        context become active(nextWriter, nextStream, 0L)
      } else {
        context become active(writer, stream, bytesWritten + value.getLength)
      }

    case HdfsWriter.Close =>
      log.debug("Close writer")
      writer.hflush()
      writer.close()
      stream.close()
      context become receive
  }

  private def nextWriterAndStream() = {
    val now = DateTime.now
    val df = new DecimalFormat("00")
    val file = Seq(config.filePrefix, now.getMillis, config.fileSuffix) mkString ""
    val path = new Path(Seq(
      "%Y" -> now.getYear.toString,
      "%m" -> df.format(now.getMonthOfYear),
      "%d" -> df.format(now.getDayOfMonth)
    ).foldLeft(config.path + "/" + file) {_.replaceAll _ tupled(_)})
    val stream = dfs.create(path)
    (SequenceFile.createWriter(dfs.getConf,
      SequenceFile.Writer.stream(stream),
      SequenceFile.Writer.keyClass(classOf[LongWritable]),
      SequenceFile.Writer.valueClass(classOf[BytesWritable])), stream)
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

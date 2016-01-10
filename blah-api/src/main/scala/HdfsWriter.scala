package blah.api

import scala.concurrent.duration._
import java.text.DecimalFormat
import akka.actor._
import spray.json._
import org.joda.time.DateTime
import org.apache.hadoop.fs.{FileSystem, Path, FSDataOutputStream}
import org.apache.hadoop.io.{SequenceFile, LongWritable, BytesWritable}
import blah.core._
import JsonProtocol._

class HdfsWriter(
  dfs: FileSystem,
  config: HdfsWriterConfig
) extends Actor with ActorLogging {
  import context.dispatcher

  def receive = {
    case m@HdfsWriter.Write(e) =>
      val timer = context.system.scheduler
        .scheduleOnce(config.closeDelay, self, HdfsWriter.Close)
      val (writer, stream) = nextWriterAndStream
      context become active(writer, stream, timer, 0L)
      self forward m
  }

  def active(
    writer: SequenceFile.Writer,
    stream: FSDataOutputStream,
    timer: Cancellable,
    bytesWritten: Long
  ): Receive = {
    case HdfsWriter.Write(e) =>
      log.debug("Write event")
      val value = new BytesWritable(e.toJson.compactPrint.getBytes)
      writer.append(new LongWritable(0L), value)
      if(bytesWritten >= config.batchSize) {
        writer.hflush()
        writer.close()
        stream.close()
        timer.cancel()
        context become receive
      } else {
        context become active(writer, stream, timer, bytesWritten + value.getLength)
      }

    case HdfsWriter.Close =>
      log.debug("Close writer")
      writer.hflush()
      writer.close()
      stream.close()
      timer.cancel()
      context become receive
  }

  private def nextWriterAndStream() = {
    val now = new DateTime
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
  batchSize: Long = 1024 * 1024 * 256,
  closeDelay: FiniteDuration = 10.seconds)

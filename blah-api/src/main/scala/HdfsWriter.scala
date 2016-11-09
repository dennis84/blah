package blah.api

import akka.actor._
import spray.json._
import org.apache.hadoop.fs.{FileSystem}
import org.apache.hadoop.io.{LongWritable, BytesWritable}
import ApiJsonProtocol._

class HdfsWriter(
  dfs: FileSystem,
  config: HdfsWriterConfig
) extends Actor with ActorLogging {
  import context.dispatcher

  def receive = {
    case m@HdfsWriter.Write(e) =>
      val timer = context.system.scheduler
        .scheduleOnce(config.closeDelay, self, HdfsWriter.Close)
      val writer = HdfsSequenceFileWriter[LongWritable, BytesWritable](dfs, config)
      context become active(writer, timer, 0L)
      self forward m
  }

  def active(
    writer: HdfsSequenceFileWriter,
    timer: Cancellable,
    bytesWritten: Long
  ): Receive = {
    case HdfsWriter.Write(e) =>
      log.debug("Write event")
      val value = new BytesWritable(e.toJson.compactPrint.getBytes)
      val nextBytesWritten = bytesWritten + value.getLength
      writer.write(new LongWritable(0L), value)
      if(nextBytesWritten >= config.batchSize) {
        writer.close()
        timer.cancel()
        context become receive
      } else {
        context become active(writer, timer, nextBytesWritten)
      }

    case HdfsWriter.Close =>
      log.debug("Close writer")
      writer.close()
      timer.cancel()
      context become receive
  }
}

object HdfsWriter {
  case class Write(event: Event)
  case object Close
}

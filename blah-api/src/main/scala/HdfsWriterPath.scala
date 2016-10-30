package blah.api

import java.text.DecimalFormat
import java.time.{ZonedDateTime, ZoneOffset}
import org.apache.hadoop.fs.Path

class HdfsWriterPath(path: String) extends Path(path) {
  def toClosed = new HdfsWriterPath(path dropRight 4)
}

object HdfsWriterPath {
  def apply(conf: HdfsWriterConfig): HdfsWriterPath = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val df = new DecimalFormat("00")
    val file = Seq(
      conf.filePrefix,
      now.toInstant.toEpochMilli,
      conf.fileSuffix) mkString ""
    val placeholders = Seq(
      "%Y" -> now.getYear.toString,
      "%m" -> df.format(now.getMonthValue),
      "%d" -> df.format(now.getDayOfMonth))
    new HdfsWriterPath((s"${conf.path}/$file.tmp" /: placeholders) {
      case (a, (k, v)) => a replaceAll (k, v)
    })
  }
}

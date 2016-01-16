package blah.api

import scala.reflect.ClassTag
import org.apache.hadoop.fs.{FileSystem, FSDataOutputStream}
import org.apache.hadoop.io.{SequenceFile, Writable}

class HdfsSequenceFileWriter(
  writer: SequenceFile.Writer,
  stream: FSDataOutputStream) {

  def write(k: Writable, v: Writable) = writer.append(k, v)

  def close() = {
    writer.hflush()
    writer.close()
    stream.close()
  }
}

object HdfsSequenceFileWriter {
  def apply[A, B](
    fs: FileSystem,
    conf: HdfsWriterConfig
  )(implicit kt: ClassTag[A], vt: ClassTag[B]): HdfsSequenceFileWriter = {
    val stream = fs.create(HdfsWriterPath(conf))
    new HdfsSequenceFileWriter(SequenceFile.createWriter(fs.getConf,
      SequenceFile.Writer.stream(stream),
      SequenceFile.Writer.keyClass(kt.runtimeClass),
      SequenceFile.Writer.valueClass(vt.runtimeClass)), stream)
  }
}

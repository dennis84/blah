package blah.core

import scala.reflect.ClassTag
import org.apache.hadoop.fs.{FileSystem, FSDataOutputStream}
import org.apache.hadoop.io.{SequenceFile, Writable}

class HdfsSequenceFileWriter(
  fs: FileSystem,
  writer: SequenceFile.Writer,
  stream: FSDataOutputStream,
  path: HdfsWriterPath) {

  def write(k: Writable, v: Writable) = writer.append(k, v)

  def close() {
    writer.hflush()
    writer.close()
    stream.close()
    fs.rename(path, path.toClosed)
  }
}

object HdfsSequenceFileWriter {
  def apply[A, B](
    fs: FileSystem,
    conf: HdfsWriterConfig
  )(implicit kt: ClassTag[A], vt: ClassTag[B]): HdfsSequenceFileWriter = {
    val path = HdfsWriterPath(conf)
    val stream = fs.create(path)
    val writer = SequenceFile.createWriter(fs.getConf,
      SequenceFile.Writer.stream(stream),
      SequenceFile.Writer.keyClass(kt.runtimeClass),
      SequenceFile.Writer.valueClass(vt.runtimeClass))
    new HdfsSequenceFileWriter(fs, writer, stream, path)
  }
}

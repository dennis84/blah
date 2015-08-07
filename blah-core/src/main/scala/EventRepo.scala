package blah.core

import scala.concurrent._
import org.apache.hadoop.fs.{FileSystem, Path}
import spray.json._

class EventRepo(
  fs: FileSystem
)(implicit e: ExecutionContext) extends JsonProtocol {

  def insert(event: Event): Future[Event] = Future {
    val fsdos = fs.create(new Path(s"blah/events/${event.id}.json"))
    fsdos.write(event.toJson.compactPrint.getBytes)
    fsdos.close
    event
  }
}

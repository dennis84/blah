package blah.api

import scala.concurrent._
import org.apache.hadoop.fs.{FileSystem, Path}
import spray.json._
import blah.core.Event

class EventRepo(
  fs: FileSystem
)(implicit e: ExecutionContext) extends ApiJsonProtocol {

  def insert(event: Event): Future[Event] = Future {
    val fsdos = fs.create(new Path(s"blah/events/${event.id}.json"))
    fsdos.write(event.toJson.compactPrint.getBytes)
    fsdos.close
    event
  }
}

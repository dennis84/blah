package blah.core

import scala.concurrent._
import scala.collection.JavaConversions._
import com.datastax.driver.core.Session
import spray.json._

class EventRepo(session: Session) {

  def insert(event: Event)(implicit ec: ExecutionContext): Future[Unit] = Future {
    val stmt = session.prepare("INSERT INTO events(id, name, props) VALUES (?, ?, ?);")
    val props: java.util.Map[String, String] = event.props map {
      case (key, JsString(value)) => key -> value
      case (key, value)           => key -> value.toString
    }

    session.executeAsync(stmt.bind(event.id, event.name, props))
  }
}

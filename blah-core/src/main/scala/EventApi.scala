package blah.core

import akka.actor._
import spray.json._
import akka.pattern.pipe
import com.github.nscala_time.time.Imports._

class EventApi(producer: Producer[String]) extends Actor with JsonProtocol {
  implicit val executor = context.dispatcher

  def receive = {
    case EventApi.Create(name, props) => {
      val evt = Event(name, DateTime.now, props)
      producer.send(evt.toJson.compactPrint)
      sender ! evt
    }
  }
}

object EventApi {
  case class Create(
    name: String,
    props: Map[String, JsValue] = Map.empty)
}

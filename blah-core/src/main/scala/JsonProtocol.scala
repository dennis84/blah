package blah.core

import spray.json._
import akka.http.scaladsl.model.DateTime

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val dateTime = new RootJsonFormat[DateTime] {
    def write(d: DateTime): JsValue =
      JsString(d.toIsoDateTimeString)
    def read(v: JsValue): DateTime =
      DateTime.fromIsoDateTimeString(v.toString).get
  }

  implicit val createEvent = jsonFormat2(CreateEvent)
  implicit val event = jsonFormat4(Event)
}

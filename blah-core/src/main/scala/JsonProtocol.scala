package blah.core

import spray.json._
import org.joda.time.DateTime

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val dateTime = new RootJsonFormat[DateTime] {
    def write(d: DateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): DateTime = v match {
      case JsString(x) => new DateTime(x)
      case _ => throw new DeserializationException("fail")
    }
  }

  implicit val createEvent = jsonFormat2(EventApi.Create)
  implicit val event = jsonFormat4(Event)
}

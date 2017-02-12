package blah.api

import java.time.ZonedDateTime
import spray.json._

object ApiJsonProtocol extends DefaultJsonProtocol {
  implicit val zonedDateTimeFmt = new RootJsonFormat[ZonedDateTime] {
    def write(d: ZonedDateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): ZonedDateTime = v match {
      case JsString(x) => ZonedDateTime.parse(x)
      case _ => throw new DeserializationException("fail")
    }
  }

  implicit val eventFmt = jsonFormat4(Event)
  implicit val messageFmt = jsonFormat1(Message)
}

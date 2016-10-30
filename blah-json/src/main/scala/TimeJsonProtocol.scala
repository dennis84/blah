package blah.json

import spray.json._
import java.time.ZonedDateTime

trait TimeJsonProtocol extends DefaultJsonProtocol {
  implicit val zonedDateTimeFmt = new RootJsonFormat[ZonedDateTime] {
    def write(d: ZonedDateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): ZonedDateTime = v match {
      case JsString(x) => ZonedDateTime.parse(x)
      case _ => throw new DeserializationException("fail")
    }
  }
}

object TimeJsonProtocol extends TimeJsonProtocol

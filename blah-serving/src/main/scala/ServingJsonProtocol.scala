package blah.serving

import spray.json._

trait ServingJsonProtocol extends DefaultJsonProtocol {
  implicit val zonedDateTimeFmt = new RootJsonFormat[ZonedDateTime] {
    def write(d: ZonedDateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): ZonedDateTime = v match {
      case JsString(x) => ZonedDateTime.parse(x)
      case _ => throw new DeserializationException("fail")
    }
  }

  implicit val filterFmt = jsonFormat3(Filter)
  implicit val messageFmt = jsonFormat1(Message)
}

object ServingJsonProtocol extends ServingJsonProtocol

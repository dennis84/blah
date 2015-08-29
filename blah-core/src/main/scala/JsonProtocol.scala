package blah.core

import spray.json._
import org.joda.time.DateTime

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val dateTimeFmt = new RootJsonFormat[DateTime] {
    def write(d: DateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): DateTime = v match {
      case JsString(x) => new DateTime(x)
      case _ => throw new DeserializationException("fail")
    }
  }

  implicit val eventFmt = jsonFormat4(Event)
  implicit val viewPropsFmt = jsonFormat2(ViewProps)
  implicit val viewEventFmt = jsonFormat4(ViewEvent)

  implicit val browserFmt = jsonFormat4(Browser)
  implicit val osFmt = jsonFormat5(OS)
  implicit val deviceFmt = jsonFormat1(Device)
  implicit val userAgentFmt = jsonFormat3(UserAgent.apply)
}

object JsonProtocol extends JsonProtocol

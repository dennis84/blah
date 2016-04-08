package blah.core

import spray.json._
import java.time.ZonedDateTime

trait JsonProtocol extends DefaultJsonProtocol {
  implicit val zonedDateTimeFmt = new RootJsonFormat[ZonedDateTime] {
    def write(d: ZonedDateTime): JsValue = JsString(d.toString)
    def read(v: JsValue): ZonedDateTime = v match {
      case JsString(x) => ZonedDateTime.parse(x)
      case _ => throw new DeserializationException("fail")
    }
  }

  implicit val browserFmt = jsonFormat4(Browser)
  implicit val osFmt = jsonFormat5(OS)
  implicit val deviceFmt = jsonFormat1(Device)
  implicit val userAgentFmt = jsonFormat3(UserAgent.apply)

  implicit val eventFmt = jsonFormat4(Event)
  implicit val viewPropsFmt = jsonFormat5(ViewProps)
  implicit val viewEventFmt = jsonFormat4(ViewEvent)
  implicit val userPropsFmt = jsonFormat4(UserProps)
  implicit val userEventFmt = jsonFormat3(UserEvent)

  implicit val messageFmt = jsonFormat1(Message)
}

object JsonProtocol extends JsonProtocol

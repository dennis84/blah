package blah.algo

import spray.json._

object UserAgentJsonProtocol extends DefaultJsonProtocol {
  implicit val browserFmt = jsonFormat4(Browser)
  implicit val osFmt = jsonFormat5(OS)
  implicit val deviceFmt = jsonFormat1(Device)
  implicit val userAgentFmt = jsonFormat3(UserAgent.apply)
}

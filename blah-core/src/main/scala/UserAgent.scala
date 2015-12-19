package blah.core

import ua_parser.Parser

object UserAgent {
  def apply(text: String): UserAgent = {
    val ua = (new Parser).parse(text)
    UserAgent(
      browser = Browser(
        family = ua.userAgent.family,
        major = Option(ua.userAgent.major),
        minor = Option(ua.userAgent.minor),
        patch = Option(ua.userAgent.patch)),
      os = OS(
        family = ua.os.family,
        major = Option(ua.os.major),
        minor = Option(ua.os.minor),
        patch = Option(ua.os.patch),
        patchMinor = Option(ua.os.patchMinor)),
      device = Device(ua.device.family)
    )
  }
}

case class UserAgent(
  browser: Browser,
  os: OS,
  device: Device)

case class Device(family: String)

case class OS(
  family: String,
  major: Option[String] = None,
  minor: Option[String] = None,
  patch: Option[String] = None,
  patchMinor: Option[String] = None)

case class Browser(
  family: String,
  major: Option[String] = None,
  minor: Option[String] = None,
  patch: Option[String] = None)

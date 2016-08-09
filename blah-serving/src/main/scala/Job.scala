package blah.serving

import java.time.ZonedDateTime

case class Job(
  name: String,
  status: String,
  repeat: Boolean = false,
  lastSuccess: Option[ZonedDateTime] = None)

case class ChronosJob(
  name: String,
  schedule: String,
  lastSuccess: String) {

  def toJob(status: Option[String]) = Job(
    name = name,
    status = status.getOrElse("idle"),
    repeat = !schedule.startsWith("R0/"),
    lastSuccess = lastSuccess match {
      case x if !x.isEmpty => Some(ZonedDateTime parse x)
      case _ => None
    })
}

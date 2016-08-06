package blah.serving

import java.time.ZonedDateTime

case class Job(
  name: String,
  repeat: Boolean = false,
  lastSuccess: Option[ZonedDateTime] = None)

case class ChronosJob(
  name: String,
  schedule: String,
  lastSuccess: String) {

  def toJob() = Job(
    name = name,
    repeat = !schedule.startsWith("R0/"),
    lastSuccess = lastSuccess match {
      case x if !x.isEmpty => Some(ZonedDateTime parse x)
      case _ => None
    })
}

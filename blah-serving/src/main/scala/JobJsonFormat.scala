package blah.serving

import spray.json.NullOptions

trait JobJsonFormat extends ServingJsonProtocol with NullOptions {
  implicit val jobFmt = jsonFormat4(Job)
  implicit val chronosJobFmt = jsonFormat3(ChronosJob)
}

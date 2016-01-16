package blah.api

import org.scalatest._

class HdfsWriterPathSpec extends FlatSpec with Matchers {
  "A HdfsWriterPath" should "be configurable with date placeholders" in {
    val path = HdfsWriterPath(HdfsWriterConfig(
      path = "/events/%Y/%m/%d",
      filePrefix = "",
      fileSuffix = "")).toString
    path should fullyMatch regex """/events/\d{4}/\d{2}/\d{2}/\d+""".r
  }

  it should "be configurable with prefix and suffix" in {
    val path = HdfsWriterPath(HdfsWriterConfig(
      path = "/events",
      filePrefix = "events",
      fileSuffix = ".jsonl")).toString
    path should fullyMatch regex """/events/events\d+.jsonl""".r
  }
}

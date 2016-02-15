package blah.core

import scala.concurrent.duration._

case class HdfsWriterConfig(
  path: String = "/events/%Y/%m/%d",
  filePrefix: String = "events.",
  fileSuffix: String = ".jsonl",
  batchSize: Long = 1024 * 1024 * 256,
  closeDelay: FiniteDuration = 10.seconds)

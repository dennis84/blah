import sbt._
import sbtassembly._

object Assembly {
  val defaultMergeStrategy: String => MergeStrategy = {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case "reference.conf" => MergeStrategy.concat
    case _ => MergeStrategy.first
  }
}

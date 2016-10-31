import sbt._
import Keys._

object Dependencies {

  val scalatest = "org.scalatest" %% "scalatest" % "3.0.0"
  val scalactic = "org.scalactic" %% "scalactic" % "3.0.0"
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.3.0"

  val geoip2 = "com.maxmind.geoip2" % "geoip2" % "2.5.0"

  val `logstash-jsonevent` = "net.logstash.log4j" % "jsonevent-layout" % "1.7"

  val `spray-json` = "io.spray" %% "spray-json" % "1.3.2"

  val `json-lenses` = "net.virtual-void" %% "json-lenses" % "0.6.1"

  val `ua-parser` = "org.clojars.timewarrior" % "ua-parser" % "1.3.0"

  val `akka-actor` = Seq(
    "com.typesafe.akka" %% "akka-actor",
    "com.typesafe.akka" %% "akka-testkit",
    "com.typesafe.akka" %% "akka-slf4j"
  ).map(_ % "2.4.8")

  val `akka-stream` = Seq(
    "com.typesafe.akka" %% "akka-stream",
    "com.typesafe.akka" %% "akka-stream-testkit"
  ).map(_ % "2.4.8")
  
  val `akka-stream-kafka` = Seq(
    "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-M4"
  )

  val `akka-http` = Seq(
    "com.typesafe.akka" %% "akka-http-core",
    "com.typesafe.akka" %% "akka-http-testkit",
    "com.typesafe.akka" %% "akka-http-experimental",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"
  ).map(_ % "2.4.8")

  val `logback-logstash` = Seq(
    "ch.qos.logback"       % "logback-classic"          % "1.1.7",
    "net.logstash.logback" % "logstash-logback-encoder" % "4.7"
  )

  val `spark-streaming-mlib` = Seq(
    "org.apache.spark" %% "spark-core"                 % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-streaming"            % "2.0.0" % "provided",
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.0.0",
    "org.apache.spark" %% "spark-mllib"                % "2.0.0"
  )
}

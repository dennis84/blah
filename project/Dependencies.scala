import sbt._
import Keys._

object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.0"
  val scalactic = "org.scalactic" %% "scalactic" % "3.0.0"
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.3.0"

  val geoip2 = "com.maxmind.geoip2" % "geoip2" % "2.5.0"

  val logstashJsonevent = "net.logstash.log4j" % "jsonevent-layout" % "1.7"

  val sprayJson = "io.spray" %% "spray-json" % "1.3.2"

  val jsonLenses = "net.virtual-void" %% "json-lenses" % "0.6.1"

  val uaParser = "org.clojars.timewarrior" % "ua-parser" % "1.3.0"

  val akkaActor         = "com.typesafe.akka" %% "akka-actor"                        % "2.4.8"
  val akkaTestkit       = "com.typesafe.akka" %% "akka-testkit"                      % "2.4.8"
  val akkaSlf4j         = "com.typesafe.akka" %% "akka-slf4j"                        % "2.4.8"
  val akkaStream        = "com.typesafe.akka" %% "akka-stream"                       % "2.4.8"
  val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit"               % "2.4.8"
  val akkaHttpCore      = "com.typesafe.akka" %% "akka-http-core"                    % "2.4.8"
  val akkaHttpTestkit   = "com.typesafe.akka" %% "akka-http-testkit"                 % "2.4.8"
  val akkaHttp          = "com.typesafe.akka" %% "akka-http-experimental"            % "2.4.8"
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.8"
  val akkaStreamKafka   = "com.typesafe.akka" %% "akka-stream-kafka"                 % "0.11-M4"

  val logbackClassic         = "ch.qos.logback"       % "logback-classic"          % "1.1.7"
  val logstashLogbackEncoder = "net.logstash.logback" % "logstash-logback-encoder" % "4.7"

  val sparkCore           = "org.apache.spark" %% "spark-core"                 % "2.0.0"
  val sparkStreaming      = "org.apache.spark" %% "spark-streaming"            % "2.0.0"
  val sparkStreamingKafka = "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.0.0"
  val sparkMllib          = "org.apache.spark" %% "spark-mllib"                % "2.0.0"
}
